"""
Convert DevOptionsScreen.kt from Column+verticalScroll to LazyColumn.
Properly tracks both braces {} and parentheses () to identify statement boundaries.
"""

filepath = r'app\src\main\java\com\kyilmaz\neurocomet\DevOptionsScreen.kt'

with open(filepath, 'r', encoding='utf-8') as f:
    lines = f.readlines()

output = []
i = 0
in_body = False

# Phase 1: Add LazyColumn import after layout.width import
for idx, line in enumerate(lines):
    if 'import androidx.compose.foundation.layout.width' in line:
        output.append(line)
        output.append('import androidx.compose.foundation.lazy.LazyColumn\n')
        i = idx + 1
        break
    output.append(line)
    i = idx + 1

# Phase 2: Copy until we hit the Column + verticalScroll, then transform
while i < len(lines):
    line = lines[i]

    if not in_body and '.verticalScroll(rememberScrollState())' in line:
        # Found the scroll modifier. Go back to find Column(
        j = len(output) - 1
        while j >= 0 and 'Column(' not in output[j]:
            j -= 1

        if j >= 0:
            before = output[:j]
            output = before
            output.append('        LazyColumn(\n')
            output.append('            modifier = Modifier\n')
            output.append('                .fillMaxSize()\n')
            output.append('                .padding(paddingValues)\n')
            output.append('                .padding(horizontal = 16.dp),\n')
            output.append('            verticalArrangement = Arrangement.spacedBy(16.dp),\n')
            output.append('            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)\n')

            # Skip lines until we find ") {"
            i += 1
            while i < len(lines):
                if lines[i].strip().startswith(') {') or lines[i].strip() == ') {':
                    output.append('        ) {\n')
                    i += 1
                    in_body = True
                    break
                elif 'verticalArrangement' in lines[i] or '.padding(16.dp)' in lines[i]:
                    i += 1  # skip these, already handled
                else:
                    i += 1
            continue

    if not in_body:
        output.append(line)
        i += 1
        continue

    # === IN BODY: wrap top-level composable calls in item { } ===
    stripped = line.strip()
    indent = len(line) - len(line.lstrip()) if stripped else 999

    # Empty lines and comments - pass through
    if stripped == '' or stripped.startswith('//') or stripped.startswith('// ═'):
        output.append(line)
        i += 1
        continue

    # Check if this is the closing brace of the LazyColumn body
    # It's at indent 8 (2 levels: Scaffold lambda + Column/LazyColumn)
    if indent <= 8 and stripped == '}':
        output.append(line)
        in_body = False
        i += 1
        while i < len(lines):
            output.append(lines[i])
            i += 1
        break

    # Direct children are at indent 12
    if indent == 12:
        # Check for if-blocks that need special handling
        if stripped.startswith('if ('):
            # Collect the entire if/else block
            block_lines, i = collect_block(lines, i)
            # Process inner content
            emit_if_block(output, block_lines)
            continue

        # Regular statement - collect and wrap in item { }
        stmt_lines, i = collect_statement(lines, i)
        output.append('            item {\n')
        for sl in stmt_lines:
            output.append(sl)
        output.append('            }\n')
        continue

    # Anything else - pass through
    output.append(line)
    i += 1

with open(filepath, 'w', encoding='utf-8') as f:
    f.writelines(output)

print(f"Done! Wrote {len(output)} lines")


def collect_statement(lines, start):
    """Collect a complete statement starting at `start`, tracking both {} and ()."""
    result = [lines[start]]
    line = lines[start]
    brace_depth = line.count('{') - line.count('}')
    paren_depth = line.count('(') - line.count(')')
    i = start + 1

    while i < len(lines) and (brace_depth > 0 or paren_depth > 0):
        result.append(lines[i])
        brace_depth += lines[i].count('{') - lines[i].count('}')
        paren_depth += lines[i].count('(') - lines[i].count(')')
        i += 1

    return result, i


def collect_block(lines, start):
    """Collect an if/else block including all braces."""
    result = [lines[start]]
    line = lines[start]
    brace_depth = line.count('{') - line.count('}')
    paren_depth = line.count('(') - line.count(')')
    i = start + 1

    while i < len(lines) and (brace_depth > 0 or paren_depth > 0):
        result.append(lines[i])
        brace_depth += lines[i].count('{') - lines[i].count('}')
        paren_depth += lines[i].count('(') - lines[i].count(')')
        i += 1

    # Check for else clause
    while i < len(lines) and lines[i].strip() == '':
        i += 1  # skip blank lines

    if i < len(lines) and (lines[i].strip().startswith('else') or lines[i].strip().startswith('} else')):
        while i < len(lines):
            result.append(lines[i])
            brace_depth += lines[i].count('{') - lines[i].count('}')
            paren_depth += lines[i].count('(') - lines[i].count(')')
            i += 1
            if brace_depth <= 0 and paren_depth <= 0:
                break

    return result, i


def emit_if_block(output, block_lines):
    """Process an if-block, wrapping inner composable calls in item { }."""
    # First line is the if (...) {
    output.append(block_lines[0])

    i = 1
    while i < len(block_lines):
        line = block_lines[i]
        stripped = line.strip()
        indent = len(line) - len(line.lstrip()) if stripped else 999

        # Closing brace of the if block
        if indent <= 12 and stripped.startswith('}'):
            output.append(line)
            i += 1
            continue

        # else clause
        if stripped.startswith('} else') or stripped == 'else {':
            output.append(line)
            i += 1
            continue

        # Empty/comment
        if stripped == '' or stripped.startswith('//') or stripped.startswith('// ═'):
            output.append(line)
            i += 1
            continue

        # Inner content at indent 16 (direct child of if block)
        if indent == 16:
            # Collect the full statement
            stmt = [line]
            bd = line.count('{') - line.count('}')
            pd = line.count('(') - line.count(')')
            i += 1
            while i < len(block_lines) and (bd > 0 or pd > 0):
                stmt.append(block_lines[i])
                bd += block_lines[i].count('{') - block_lines[i].count('}')
                pd += block_lines[i].count('(') - block_lines[i].count(')')
                i += 1

            # Wrap in item { }
            output.append('                item {\n')
            for sl in stmt:
                output.append(sl)
            output.append('                }\n')
            continue

        output.append(line)
        i += 1

