import { createClient } from 'https://esm.sh/@supabase/supabase-js@2';

type ClaimedDeletion = {
  id: string;
  email: string | null;
  deletion_scheduled_at: string | null;
  deletion_attempt_count: number;
};

const corsHeaders = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type, x-account-deletion-secret',
};

function json(status: number, body: unknown) {
  return new Response(JSON.stringify(body, null, 2), {
    status,
    headers: {
      ...corsHeaders,
      'Content-Type': 'application/json',
    },
  });
}

function getSecretFromRequest(req: Request): string | null {
  const bearer = req.headers.get('authorization');
  if (bearer?.startsWith('Bearer ')) {
    return bearer.slice('Bearer '.length).trim();
  }
  return req.headers.get('x-account-deletion-secret')?.trim() ?? null;
}

Deno.serve(async (req) => {
  if (req.method === 'OPTIONS') {
    return new Response('ok', { headers: corsHeaders });
  }

  const supabaseUrl = Deno.env.get('SUPABASE_URL');
  const serviceRoleKey = Deno.env.get('SUPABASE_SERVICE_ROLE_KEY');
  const cronSecret = Deno.env.get('ACCOUNT_DELETION_CRON_SECRET');

  if (!supabaseUrl || !serviceRoleKey) {
    return json(500, {
      success: false,
      message: 'Missing SUPABASE_URL and/or SUPABASE_SERVICE_ROLE_KEY environment variables.',
    });
  }

  if (cronSecret) {
    const requestSecret = getSecretFromRequest(req);
    if (requestSecret !== cronSecret) {
      return json(401, {
        success: false,
        message: 'Unauthorized. Provide ACCOUNT_DELETION_CRON_SECRET as a bearer token or x-account-deletion-secret header.',
      });
    }
  }

  let batchSize = 10;
  try {
    if (req.method !== 'GET') {
      const body = await req.json().catch(() => ({}));
      const parsed = Number(body?.batchSize);
      if (Number.isFinite(parsed) && parsed > 0) {
        batchSize = Math.min(parsed, 100);
      }
    } else {
      const parsed = Number(new URL(req.url).searchParams.get('batchSize'));
      if (Number.isFinite(parsed) && parsed > 0) {
        batchSize = Math.min(parsed, 100);
      }
    }
  } catch {
    // keep default batch size
  }

  const admin = createClient(supabaseUrl, serviceRoleKey, {
    auth: {
      autoRefreshToken: false,
      persistSession: false,
    },
  });

  const { data: claimedRows, error: claimError } = await admin.rpc('claim_due_account_deletions', {
    batch_size: batchSize,
  });

  if (claimError) {
    return json(500, {
      success: false,
      stage: 'claim',
      message: claimError.message,
      details: claimError,
    });
  }

  const claimed = (claimedRows ?? []) as ClaimedDeletion[];
  const summary = {
    success: true,
    claimedCount: claimed.length,
    deleted: [] as Array<Record<string, unknown>>,
    failed: [] as Array<Record<string, unknown>>,
  };

  for (const row of claimed) {
    try {
      const deleteResult = await admin.auth.admin.deleteUser(row.id);
      const deleteError = deleteResult.error;
      const userAlreadyGone = deleteError?.message?.toLowerCase().includes('not found') === true;
      if (deleteError && !userAlreadyGone) {
        throw deleteError;
      }

      const { error: purgeError } = await admin.rpc('purge_account_data', {
        target_user_id: row.id,
      });
      if (purgeError) {
        throw purgeError;
      }

      summary.deleted.push({
        id: row.id,
        email: row.email,
        deletionScheduledAt: row.deletion_scheduled_at,
        deletionAttemptCount: row.deletion_attempt_count,
        authUserDeleted: !userAlreadyGone,
      });
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      await admin.rpc('mark_account_deletion_failed', {
        target_user_id: row.id,
        error_message: message,
      });

      summary.failed.push({
        id: row.id,
        email: row.email,
        deletionScheduledAt: row.deletion_scheduled_at,
        deletionAttemptCount: row.deletion_attempt_count,
        error: message,
      });
    }
  }

  return json(200, summary);
});

