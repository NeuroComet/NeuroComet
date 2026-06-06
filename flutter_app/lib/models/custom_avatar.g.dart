// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'custom_avatar.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

CustomAvatar _$CustomAvatarFromJson(Map<String, dynamic> json) => CustomAvatar(
  id: json['id'] as String,
  shape:
      $enumDecodeNullable(_$AvatarShapeEnumMap, json['shape']) ??
      AvatarShape.circle,
  backgroundColor: json['backgroundColor'] as String? ?? '#7C4DFF',
  skinColor: json['skinColor'] as String? ?? '#FFDAB9',
  hairStyle:
      $enumDecodeNullable(_$AvatarHairStyleEnumMap, json['hairStyle']) ??
      AvatarHairStyle.short,
  hairColor: json['hairColor'] as String? ?? '#3D2314',
  eyeStyle:
      $enumDecodeNullable(_$AvatarEyeStyleEnumMap, json['eyeStyle']) ??
      AvatarEyeStyle.normal,
  eyeColor: json['eyeColor'] as String? ?? '#634E34',
  mouthStyle:
      $enumDecodeNullable(_$AvatarMouthStyleEnumMap, json['mouthStyle']) ??
      AvatarMouthStyle.smile,
  accessory: $enumDecodeNullable(_$AvatarAccessoryEnumMap, json['accessory']),
  accessoryColor: json['accessoryColor'] as String?,
  facialHair: $enumDecodeNullable(
    _$AvatarFacialHairEnumMap,
    json['facialHair'],
  ),
  facialHairColor: json['facialHairColor'] as String?,
  createdAt: DateTime.parse(json['createdAt'] as String),
);

Map<String, dynamic> _$CustomAvatarToJson(CustomAvatar instance) =>
    <String, dynamic>{
      'id': instance.id,
      'shape': _$AvatarShapeEnumMap[instance.shape]!,
      'backgroundColor': instance.backgroundColor,
      'skinColor': instance.skinColor,
      'hairStyle': _$AvatarHairStyleEnumMap[instance.hairStyle]!,
      'hairColor': instance.hairColor,
      'eyeStyle': _$AvatarEyeStyleEnumMap[instance.eyeStyle]!,
      'eyeColor': instance.eyeColor,
      'mouthStyle': _$AvatarMouthStyleEnumMap[instance.mouthStyle]!,
      'accessory': _$AvatarAccessoryEnumMap[instance.accessory],
      'accessoryColor': instance.accessoryColor,
      'facialHair': _$AvatarFacialHairEnumMap[instance.facialHair],
      'facialHairColor': instance.facialHairColor,
      'createdAt': instance.createdAt.toIso8601String(),
    };

const _$AvatarShapeEnumMap = {
  AvatarShape.circle: 'circle',
  AvatarShape.rounded: 'rounded',
  AvatarShape.square: 'square',
};

const _$AvatarHairStyleEnumMap = {
  AvatarHairStyle.none: 'none',
  AvatarHairStyle.short: 'short',
  AvatarHairStyle.medium: 'medium',
  AvatarHairStyle.long: 'long',
  AvatarHairStyle.curly: 'curly',
  AvatarHairStyle.wavy: 'wavy',
  AvatarHairStyle.buzz: 'buzz',
  AvatarHairStyle.ponytail: 'ponytail',
  AvatarHairStyle.bun: 'bun',
  AvatarHairStyle.mohawk: 'mohawk',
  AvatarHairStyle.afro: 'afro',
  AvatarHairStyle.spiky: 'spiky',
  AvatarHairStyle.braids: 'braids',
};

const _$AvatarEyeStyleEnumMap = {
  AvatarEyeStyle.normal: 'normal',
  AvatarEyeStyle.happy: 'happy',
  AvatarEyeStyle.sleepy: 'sleepy',
  AvatarEyeStyle.wink: 'wink',
  AvatarEyeStyle.surprised: 'surprised',
  AvatarEyeStyle.hearts: 'hearts',
  AvatarEyeStyle.stars: 'stars',
  AvatarEyeStyle.glasses: 'glasses',
  AvatarEyeStyle.sunglasses: 'sunglasses',
  AvatarEyeStyle.closed: 'closed',
};

const _$AvatarMouthStyleEnumMap = {
  AvatarMouthStyle.smile: 'smile',
  AvatarMouthStyle.grin: 'grin',
  AvatarMouthStyle.neutral: 'neutral',
  AvatarMouthStyle.sad: 'sad',
  AvatarMouthStyle.surprised: 'surprised',
  AvatarMouthStyle.tongue: 'tongue',
  AvatarMouthStyle.teeth: 'teeth',
  AvatarMouthStyle.smirk: 'smirk',
};

const _$AvatarAccessoryEnumMap = {
  AvatarAccessory.none: 'none',
  AvatarAccessory.glasses: 'glasses',
  AvatarAccessory.sunglasses: 'sunglasses',
  AvatarAccessory.hat: 'hat',
  AvatarAccessory.cap: 'cap',
  AvatarAccessory.beanie: 'beanie',
  AvatarAccessory.headband: 'headband',
  AvatarAccessory.bow: 'bow',
  AvatarAccessory.earrings: 'earrings',
  AvatarAccessory.headphones: 'headphones',
};

const _$AvatarFacialHairEnumMap = {
  AvatarFacialHair.none: 'none',
  AvatarFacialHair.stubble: 'stubble',
  AvatarFacialHair.mustache: 'mustache',
  AvatarFacialHair.goatee: 'goatee',
  AvatarFacialHair.beard: 'beard',
  AvatarFacialHair.fullBeard: 'fullBeard',
};
