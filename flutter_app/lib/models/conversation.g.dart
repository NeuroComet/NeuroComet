// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'conversation.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Conversation _$ConversationFromJson(Map<String, dynamic> json) => Conversation(
  id: json['id'] as String,
  displayName: json['displayName'] as String,
  avatarUrl: json['avatarUrl'] as String?,
  lastMessage: json['lastMessage'] as String?,
  lastMessageAt: json['lastMessageAt'] == null
      ? null
      : DateTime.parse(json['lastMessageAt'] as String),
  unreadCount: (json['unreadCount'] as num?)?.toInt() ?? 0,
  isOnline: json['isOnline'] as bool? ?? false,
  isMuted: json['isMuted'] as bool? ?? false,
  isBlocked: json['isBlocked'] as bool? ?? false,
  isGroup: json['isGroup'] as bool? ?? false,
  isPinned: json['isPinned'] as bool? ?? false,
  isPrimary: json['isPrimary'] as bool? ?? false,
  isVerified: json['isVerified'] as bool? ?? false,
  isTyping: json['isTyping'] as bool? ?? false,
  participantId: json['participantId'] as String?,
  participantIds: (json['participantIds'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList(),
  createdAt: json['createdAt'] == null
      ? null
      : DateTime.parse(json['createdAt'] as String),
  moderationStatus:
      $enumDecodeNullable(
        _$ModerationStatusEnumMap,
        json['moderationStatus'],
      ) ??
      ModerationStatus.none,
  groupName: json['groupName'] as String?,
  memberNames: (json['memberNames'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList(),
);

Map<String, dynamic> _$ConversationToJson(Conversation instance) =>
    <String, dynamic>{
      'id': instance.id,
      'displayName': instance.displayName,
      'avatarUrl': instance.avatarUrl,
      'lastMessage': instance.lastMessage,
      'lastMessageAt': instance.lastMessageAt?.toIso8601String(),
      'unreadCount': instance.unreadCount,
      'isOnline': instance.isOnline,
      'isMuted': instance.isMuted,
      'isBlocked': instance.isBlocked,
      'isGroup': instance.isGroup,
      'isPinned': instance.isPinned,
      'isPrimary': instance.isPrimary,
      'isVerified': instance.isVerified,
      'isTyping': instance.isTyping,
      'participantId': instance.participantId,
      'participantIds': instance.participantIds,
      'createdAt': instance.createdAt?.toIso8601String(),
      'moderationStatus': _$ModerationStatusEnumMap[instance.moderationStatus]!,
      'groupName': instance.groupName,
      'memberNames': instance.memberNames,
    };

const _$ModerationStatusEnumMap = {
  ModerationStatus.none: 'none',
  ModerationStatus.verified: 'verified',
  ModerationStatus.moderator: 'moderator',
  ModerationStatus.admin: 'admin',
  ModerationStatus.warned: 'warned',
  ModerationStatus.restricted: 'restricted',
};

Message _$MessageFromJson(Map<String, dynamic> json) => Message(
  id: json['id'] as String,
  conversationId: json['conversationId'] as String,
  senderId: json['senderId'] as String,
  content: json['content'] as String,
  type:
      $enumDecodeNullable(_$MessageTypeEnumMap, json['type']) ??
      MessageType.text,
  mediaUrl: json['mediaUrl'] as String?,
  status:
      $enumDecodeNullable(_$MessageStatusEnumMap, json['status']) ??
      MessageStatus.sent,
  createdAt: json['createdAt'] == null
      ? null
      : DateTime.parse(json['createdAt'] as String),
  readAt: json['readAt'] == null
      ? null
      : DateTime.parse(json['readAt'] as String),
  replyToMessageId: json['replyToMessageId'] as String?,
  reactions:
      (json['reactions'] as List<dynamic>?)
          ?.map((e) => MessageReaction.fromJson(e as Map<String, dynamic>))
          .toList() ??
      const [],
);

Map<String, dynamic> _$MessageToJson(Message instance) => <String, dynamic>{
  'id': instance.id,
  'conversationId': instance.conversationId,
  'senderId': instance.senderId,
  'content': instance.content,
  'type': _$MessageTypeEnumMap[instance.type]!,
  'mediaUrl': instance.mediaUrl,
  'status': _$MessageStatusEnumMap[instance.status]!,
  'createdAt': instance.createdAt?.toIso8601String(),
  'readAt': instance.readAt?.toIso8601String(),
  'replyToMessageId': instance.replyToMessageId,
  'reactions': instance.reactions,
};

const _$MessageTypeEnumMap = {
  MessageType.text: 'text',
  MessageType.image: 'image',
  MessageType.video: 'video',
  MessageType.audio: 'audio',
  MessageType.file: 'file',
  MessageType.sticker: 'sticker',
};

const _$MessageStatusEnumMap = {
  MessageStatus.sending: 'sending',
  MessageStatus.sent: 'sent',
  MessageStatus.delivered: 'delivered',
  MessageStatus.read: 'read',
  MessageStatus.failed: 'failed',
};

MessageReaction _$MessageReactionFromJson(Map<String, dynamic> json) =>
    MessageReaction(
      emoji: json['emoji'] as String,
      userId: json['userId'] as String,
    );

Map<String, dynamic> _$MessageReactionToJson(MessageReaction instance) =>
    <String, dynamic>{'emoji': instance.emoji, 'userId': instance.userId};
