// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'backup_metadata.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BackupScope _$BackupScopeFromJson(Map<String, dynamic> json) => BackupScope(
  includeProfile: json['includeProfile'] as bool? ?? true,
  includeMessages: json['includeMessages'] as bool? ?? true,
  includePosts: json['includePosts'] as bool? ?? true,
  includeBookmarks: json['includeBookmarks'] as bool? ?? true,
  includeFollows: json['includeFollows'] as bool? ?? true,
  includeSettings: json['includeSettings'] as bool? ?? true,
  includeNotifications: json['includeNotifications'] as bool? ?? true,
);

Map<String, dynamic> _$BackupScopeToJson(BackupScope instance) =>
    <String, dynamic>{
      'includeProfile': instance.includeProfile,
      'includeMessages': instance.includeMessages,
      'includePosts': instance.includePosts,
      'includeBookmarks': instance.includeBookmarks,
      'includeFollows': instance.includeFollows,
      'includeSettings': instance.includeSettings,
      'includeNotifications': instance.includeNotifications,
    };

BackupMetadata _$BackupMetadataFromJson(Map<String, dynamic> json) =>
    BackupMetadata(
      backupId: json['backupId'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
      appVersion: json['appVersion'] as String,
      sizeBytes: (json['sizeBytes'] as num).toInt(),
      isEncrypted: json['isEncrypted'] as bool? ?? false,
      storageLocation: $enumDecode(
        _$BackupStorageLocationEnumMap,
        json['storageLocation'],
      ),
      dataManifest: Map<String, int>.from(json['dataManifest'] as Map),
      scope: BackupScope.fromJson(json['scope'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$BackupMetadataToJson(
  BackupMetadata instance,
) => <String, dynamic>{
  'backupId': instance.backupId,
  'createdAt': instance.createdAt.toIso8601String(),
  'appVersion': instance.appVersion,
  'sizeBytes': instance.sizeBytes,
  'isEncrypted': instance.isEncrypted,
  'storageLocation': _$BackupStorageLocationEnumMap[instance.storageLocation]!,
  'dataManifest': instance.dataManifest,
  'scope': instance.scope,
};

const _$BackupStorageLocationEnumMap = {
  BackupStorageLocation.local: 'local',
  BackupStorageLocation.googleDrive: 'googleDrive',
};

BackupSettings _$BackupSettingsFromJson(Map<String, dynamic> json) =>
    BackupSettings(
      autoBackupFrequency:
          $enumDecodeNullable(
            _$BackupFrequencyEnumMap,
            json['autoBackupFrequency'],
          ) ??
          BackupFrequency.off,
      wifiOnly: json['wifiOnly'] as bool? ?? true,
      encryptBackups: json['encryptBackups'] as bool? ?? false,
      scope: json['scope'] == null
          ? const BackupScope()
          : BackupScope.fromJson(json['scope'] as Map<String, dynamic>),
      googleAccountEmail: json['googleAccountEmail'] as String?,
      lastBackupAt: json['lastBackupAt'] == null
          ? null
          : DateTime.parse(json['lastBackupAt'] as String),
      lastBackupId: json['lastBackupId'] as String?,
      lastBackupSizeBytes: (json['lastBackupSizeBytes'] as num?)?.toInt(),
    );

Map<String, dynamic> _$BackupSettingsToJson(BackupSettings instance) =>
    <String, dynamic>{
      'autoBackupFrequency':
          _$BackupFrequencyEnumMap[instance.autoBackupFrequency]!,
      'wifiOnly': instance.wifiOnly,
      'encryptBackups': instance.encryptBackups,
      'scope': instance.scope,
      'googleAccountEmail': instance.googleAccountEmail,
      'lastBackupAt': instance.lastBackupAt?.toIso8601String(),
      'lastBackupId': instance.lastBackupId,
      'lastBackupSizeBytes': instance.lastBackupSizeBytes,
    };

const _$BackupFrequencyEnumMap = {
  BackupFrequency.off: 'off',
  BackupFrequency.daily: 'daily',
  BackupFrequency.weekly: 'weekly',
  BackupFrequency.monthly: 'monthly',
};
