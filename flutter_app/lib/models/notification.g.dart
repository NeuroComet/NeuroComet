// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

AppNotification _$AppNotificationFromJson(Map<String, dynamic> json) =>
    AppNotification(
      id: json['id'] as String,
      type: $enumDecode(_$NotificationTypeEnumMap, json['type']),
      title: json['title'] as String?,
      message: json['message'] as String,
      actorId: json['actorId'] as String?,
      actorName: json['actorName'] as String?,
      actorAvatarUrl: json['actorAvatarUrl'] as String?,
      targetId: json['targetId'] as String?,
      targetType: json['targetType'] as String?,
      actionUrl: json['actionUrl'] as String?,
      relatedPostId: (json['relatedPostId'] as num?)?.toInt(),
      isRead: json['isRead'] as bool? ?? false,
      createdAt: json['createdAt'] == null
          ? null
          : DateTime.parse(json['createdAt'] as String),
    );

Map<String, dynamic> _$AppNotificationToJson(AppNotification instance) =>
    <String, dynamic>{
      'id': instance.id,
      'type': _$NotificationTypeEnumMap[instance.type]!,
      'title': instance.title,
      'message': instance.message,
      'actorId': instance.actorId,
      'actorName': instance.actorName,
      'actorAvatarUrl': instance.actorAvatarUrl,
      'targetId': instance.targetId,
      'targetType': instance.targetType,
      'actionUrl': instance.actionUrl,
      'relatedPostId': instance.relatedPostId,
      'isRead': instance.isRead,
      'createdAt': instance.createdAt?.toIso8601String(),
    };

const _$NotificationTypeEnumMap = {
  NotificationType.like: 'like',
  NotificationType.comment: 'comment',
  NotificationType.follow: 'follow',
  NotificationType.mention: 'mention',
  NotificationType.message: 'message',
  NotificationType.achievement: 'achievement',
  NotificationType.system: 'system',
  NotificationType.repost: 'repost',
  NotificationType.badge: 'badge',
  NotificationType.welcome: 'welcome',
  NotificationType.safetyAlert: 'safety_alert',
};
