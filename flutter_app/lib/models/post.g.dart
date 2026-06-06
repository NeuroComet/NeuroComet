// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'post.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Post _$PostFromJson(Map<String, dynamic> json) => Post(
  id: json['id'] as String,
  authorId: json['authorId'] as String,
  authorName: json['authorName'] as String,
  authorAvatarUrl: json['authorAvatarUrl'] as String?,
  content: json['content'] as String,
  mediaUrls: (json['mediaUrls'] as List<dynamic>?)
      ?.map((e) => e as String)
      .toList(),
  likeCount: (json['likeCount'] as num?)?.toInt() ?? 0,
  commentCount: (json['commentCount'] as num?)?.toInt() ?? 0,
  shareCount: (json['shareCount'] as num?)?.toInt() ?? 0,
  isLiked: json['isLiked'] as bool? ?? false,
  isBookmarked: json['isBookmarked'] as bool? ?? false,
  category: json['category'] as String?,
  tags: (json['tags'] as List<dynamic>?)?.map((e) => e as String).toList(),
  createdAt: json['createdAt'] == null
      ? null
      : DateTime.parse(json['createdAt'] as String),
  updatedAt: json['updatedAt'] == null
      ? null
      : DateTime.parse(json['updatedAt'] as String),
  moderationStatus: json['moderationStatus'] as String? ?? 'clean',
  backgroundColor: (json['backgroundColor'] as num?)?.toInt(),
  tone: json['tone'] as String?,
  locationTag: json['locationTag'] as String?,
);

Map<String, dynamic> _$PostToJson(Post instance) => <String, dynamic>{
  'id': instance.id,
  'authorId': instance.authorId,
  'authorName': instance.authorName,
  'authorAvatarUrl': instance.authorAvatarUrl,
  'content': instance.content,
  'mediaUrls': instance.mediaUrls,
  'likeCount': instance.likeCount,
  'commentCount': instance.commentCount,
  'shareCount': instance.shareCount,
  'isLiked': instance.isLiked,
  'isBookmarked': instance.isBookmarked,
  'category': instance.category,
  'tags': instance.tags,
  'createdAt': instance.createdAt?.toIso8601String(),
  'updatedAt': instance.updatedAt?.toIso8601String(),
  'moderationStatus': instance.moderationStatus,
  'backgroundColor': instance.backgroundColor,
  'tone': instance.tone,
  'locationTag': instance.locationTag,
};

Comment _$CommentFromJson(Map<String, dynamic> json) => Comment(
  id: json['id'] as String,
  postId: json['postId'] as String,
  authorId: json['authorId'] as String,
  authorName: json['authorName'] as String,
  authorAvatarUrl: json['authorAvatarUrl'] as String?,
  content: json['content'] as String,
  likeCount: (json['likeCount'] as num?)?.toInt() ?? 0,
  isLiked: json['isLiked'] as bool? ?? false,
  parentCommentId: json['parentCommentId'] as String?,
  replies:
      (json['replies'] as List<dynamic>?)
          ?.map((e) => Comment.fromJson(e as Map<String, dynamic>))
          .toList() ??
      const [],
  createdAt: json['createdAt'] == null
      ? null
      : DateTime.parse(json['createdAt'] as String),
);

Map<String, dynamic> _$CommentToJson(Comment instance) => <String, dynamic>{
  'id': instance.id,
  'postId': instance.postId,
  'authorId': instance.authorId,
  'authorName': instance.authorName,
  'authorAvatarUrl': instance.authorAvatarUrl,
  'content': instance.content,
  'likeCount': instance.likeCount,
  'isLiked': instance.isLiked,
  'parentCommentId': instance.parentCommentId,
  'replies': instance.replies,
  'createdAt': instance.createdAt?.toIso8601String(),
};

Story _$StoryFromJson(Map<String, dynamic> json) => Story(
  id: json['id'] as String,
  authorId: json['authorId'] as String,
  authorName: json['authorName'] as String,
  authorAvatarUrl: json['authorAvatarUrl'] as String?,
  items: (json['items'] as List<dynamic>)
      .map((e) => StoryItem.fromJson(e as Map<String, dynamic>))
      .toList(),
  isViewed: json['isViewed'] as bool? ?? false,
  createdAt: json['createdAt'] == null
      ? null
      : DateTime.parse(json['createdAt'] as String),
  expiresAt: json['expiresAt'] == null
      ? null
      : DateTime.parse(json['expiresAt'] as String),
);

Map<String, dynamic> _$StoryToJson(Story instance) => <String, dynamic>{
  'id': instance.id,
  'authorId': instance.authorId,
  'authorName': instance.authorName,
  'authorAvatarUrl': instance.authorAvatarUrl,
  'items': instance.items,
  'isViewed': instance.isViewed,
  'createdAt': instance.createdAt?.toIso8601String(),
  'expiresAt': instance.expiresAt?.toIso8601String(),
};

StoryItem _$StoryItemFromJson(Map<String, dynamic> json) => StoryItem(
  id: json['id'] as String,
  mediaUrl: json['mediaUrl'] as String,
  mediaType:
      $enumDecodeNullable(_$StoryMediaTypeEnumMap, json['mediaType']) ??
      StoryMediaType.image,
  caption: json['caption'] as String?,
  durationSeconds: (json['durationSeconds'] as num?)?.toInt() ?? 5,
  createdAt: json['createdAt'] == null
      ? null
      : DateTime.parse(json['createdAt'] as String),
);

Map<String, dynamic> _$StoryItemToJson(StoryItem instance) => <String, dynamic>{
  'id': instance.id,
  'mediaUrl': instance.mediaUrl,
  'mediaType': _$StoryMediaTypeEnumMap[instance.mediaType]!,
  'caption': instance.caption,
  'durationSeconds': instance.durationSeconds,
  'createdAt': instance.createdAt?.toIso8601String(),
};

const _$StoryMediaTypeEnumMap = {
  StoryMediaType.image: 'image',
  StoryMediaType.video: 'video',
};
