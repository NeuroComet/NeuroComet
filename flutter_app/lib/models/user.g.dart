// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

User _$UserFromJson(Map<String, dynamic> json) => User(
  id: json['id'] as String,
  displayName: json['displayName'] as String,
  username: json['username'] as String?,
  email: json['email'] as String?,
  avatarUrl: json['avatarUrl'] as String?,
  bannerUrl: json['bannerUrl'] as String?,
  bio: json['bio'] as String?,
  postCount: (json['postCount'] as num?)?.toInt() ?? 0,
  followerCount: (json['followerCount'] as num?)?.toInt() ?? 0,
  followingCount: (json['followingCount'] as num?)?.toInt() ?? 0,
  isFollowing: json['isFollowing'] as bool? ?? false,
  isFollowedBy: json['isFollowedBy'] as bool? ?? false,
  isBlocked: json['isBlocked'] as bool? ?? false,
  isPremium: json['isPremium'] as bool? ?? false,
  isVerified: json['isVerified'] as bool? ?? false,
  badges: (json['badges'] as List<dynamic>?)?.map((e) => e as String).toList(),
  createdAt: json['createdAt'] == null
      ? null
      : DateTime.parse(json['createdAt'] as String),
  lastActiveAt: json['lastActiveAt'] == null
      ? null
      : DateTime.parse(json['lastActiveAt'] as String),
  preferences: json['preferences'] == null
      ? null
      : UserPreferences.fromJson(json['preferences'] as Map<String, dynamic>),
);

Map<String, dynamic> _$UserToJson(User instance) => <String, dynamic>{
  'id': instance.id,
  'displayName': instance.displayName,
  'username': instance.username,
  'email': instance.email,
  'avatarUrl': instance.avatarUrl,
  'bannerUrl': instance.bannerUrl,
  'bio': instance.bio,
  'postCount': instance.postCount,
  'followerCount': instance.followerCount,
  'followingCount': instance.followingCount,
  'isFollowing': instance.isFollowing,
  'isFollowedBy': instance.isFollowedBy,
  'isBlocked': instance.isBlocked,
  'isPremium': instance.isPremium,
  'isVerified': instance.isVerified,
  'badges': instance.badges,
  'createdAt': instance.createdAt?.toIso8601String(),
  'lastActiveAt': instance.lastActiveAt?.toIso8601String(),
  'preferences': instance.preferences,
};

UserPreferences _$UserPreferencesFromJson(Map<String, dynamic> json) =>
    UserPreferences(
      reducedMotion: json['reducedMotion'] as bool? ?? false,
      highContrast: json['highContrast'] as bool? ?? false,
      textScale: (json['textScale'] as num?)?.toDouble() ?? 1.0,
      theme: json['theme'] as String? ?? 'system',
      language: json['language'] as String? ?? 'en',
      pushNotifications: json['pushNotifications'] as bool? ?? true,
      emailNotifications: json['emailNotifications'] as bool? ?? true,
      showOnlineStatus: json['showOnlineStatus'] as bool? ?? true,
      messagePrivacy: json['messagePrivacy'] as String? ?? 'everyone',
    );

Map<String, dynamic> _$UserPreferencesToJson(UserPreferences instance) =>
    <String, dynamic>{
      'reducedMotion': instance.reducedMotion,
      'highContrast': instance.highContrast,
      'textScale': instance.textScale,
      'theme': instance.theme,
      'language': instance.language,
      'pushNotifications': instance.pushNotifications,
      'emailNotifications': instance.emailNotifications,
      'showOnlineStatus': instance.showOnlineStatus,
      'messagePrivacy': instance.messagePrivacy,
    };

UserBadge _$UserBadgeFromJson(Map<String, dynamic> json) => UserBadge(
  id: json['id'] as String,
  name: json['name'] as String,
  icon: json['icon'] as String,
  description: json['description'] as String?,
  earnedAt: json['earnedAt'] == null
      ? null
      : DateTime.parse(json['earnedAt'] as String),
);

Map<String, dynamic> _$UserBadgeToJson(UserBadge instance) => <String, dynamic>{
  'id': instance.id,
  'name': instance.name,
  'icon': instance.icon,
  'description': instance.description,
  'earnedAt': instance.earnedAt?.toIso8601String(),
};
