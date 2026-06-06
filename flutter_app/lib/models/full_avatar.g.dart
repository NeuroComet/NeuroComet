// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'full_avatar.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

FullAvatar _$FullAvatarFromJson(Map<String, dynamic> json) => FullAvatar(
  id: json['id'] as String,
  name: json['name'] as String? ?? 'My Avatar',
  schemaVersion: (json['schemaVersion'] as num?)?.toInt() ?? 1,
  bodyMetrics: json['bodyMetrics'] == null
      ? const AvatarBodyMetrics()
      : AvatarBodyMetrics.fromJson(json['bodyMetrics'] as Map<String, dynamic>),
  bodyType:
      $enumDecodeNullable(_$AvatarBodyTypeEnumMap, json['bodyType']) ??
      AvatarBodyType.average,
  height: (json['height'] as num?)?.toDouble() ?? 0.5,
  skinTone: json['skinTone'] as String? ?? '#EECEB3',
  headShape:
      $enumDecodeNullable(_$AvatarHeadShapeEnumMap, json['headShape']) ??
      AvatarHeadShape.oval,
  faceShape:
      $enumDecodeNullable(_$AvatarFaceShapeEnumMap, json['faceShape']) ??
      AvatarFaceShape.round,
  hairStyle:
      $enumDecodeNullable(_$AvatarHairStyle2EnumMap, json['hairStyle']) ??
      AvatarHairStyle2.short,
  hairColor: json['hairColor'] as String? ?? '#3D2314',
  hairHighlights: json['hairHighlights'] as bool? ?? false,
  highlightColor: json['highlightColor'] as String?,
  eyebrowStyle:
      $enumDecodeNullable(_$AvatarEyebrowStyleEnumMap, json['eyebrowStyle']) ??
      AvatarEyebrowStyle.natural,
  eyebrowColor: json['eyebrowColor'] as String? ?? '#3D2314',
  eyebrowPosition: (json['eyebrowPosition'] as num?)?.toDouble() ?? 0.0,
  eyeShape:
      $enumDecodeNullable(_$AvatarEyeShapeEnumMap, json['eyeShape']) ??
      AvatarEyeShape.almond,
  eyeColor: json['eyeColor'] as String? ?? '#634E34',
  eyeSize: (json['eyeSize'] as num?)?.toDouble() ?? 1.0,
  eyeSpacing: (json['eyeSpacing'] as num?)?.toDouble() ?? 0.0,
  eyelashStyle:
      $enumDecodeNullable(_$AvatarEyelashStyleEnumMap, json['eyelashStyle']) ??
      AvatarEyelashStyle.natural,
  noseStyle:
      $enumDecodeNullable(_$AvatarNoseStyleEnumMap, json['noseStyle']) ??
      AvatarNoseStyle.small,
  noseSize: (json['noseSize'] as num?)?.toDouble() ?? 1.0,
  mouthShape:
      $enumDecodeNullable(_$AvatarMouthShapeEnumMap, json['mouthShape']) ??
      AvatarMouthShape.natural,
  lipColor: json['lipColor'] as String? ?? '#E8A0A0',
  mouthSize: (json['mouthSize'] as num?)?.toDouble() ?? 1.0,
  facialHair: $enumDecodeNullable(
    _$AvatarFacialHair2EnumMap,
    json['facialHair'],
  ),
  facialHairColor: json['facialHairColor'] as String?,
  facialFeatures:
      (json['facialFeatures'] as List<dynamic>?)
          ?.map((e) => $enumDecode(_$AvatarFacialFeatureEnumMap, e))
          .toList() ??
      const [],
  blushIntensity: (json['blushIntensity'] as num?)?.toDouble() ?? 0.0,
  expression:
      $enumDecodeNullable(_$AvatarExpressionEnumMap, json['expression']) ??
      AvatarExpression.happy,
  pose:
      $enumDecodeNullable(_$AvatarPoseEnumMap, json['pose']) ??
      AvatarPose.standing,
  animationProfile:
      $enumDecodeNullable(
        _$AvatarAnimationProfileEnumMap,
        json['animationProfile'],
      ) ??
      AvatarAnimationProfile.idle,
  topStyle:
      $enumDecodeNullable(_$AvatarTopStyleEnumMap, json['topStyle']) ??
      AvatarTopStyle.tshirt,
  topPrimaryColor: json['topPrimaryColor'] as String? ?? '#4A90D9',
  topSecondaryColor: json['topSecondaryColor'] as String?,
  topPattern: $enumDecodeNullable(_$AvatarPatternEnumMap, json['topPattern']),
  bottomStyle:
      $enumDecodeNullable(_$AvatarBottomStyleEnumMap, json['bottomStyle']) ??
      AvatarBottomStyle.jeans,
  bottomColor: json['bottomColor'] as String? ?? '#2C3E50',
  bottomPattern: $enumDecodeNullable(
    _$AvatarPatternEnumMap,
    json['bottomPattern'],
  ),
  outfit: $enumDecodeNullable(_$AvatarOutfitEnumMap, json['outfit']),
  outfitColor: json['outfitColor'] as String?,
  footwear:
      $enumDecodeNullable(_$AvatarFootwearEnumMap, json['footwear']) ??
      AvatarFootwear.sneakers,
  footwearColor: json['footwearColor'] as String? ?? '#FFFFFF',
  headwear: $enumDecodeNullable(_$AvatarHeadwearEnumMap, json['headwear']),
  headwearColor: json['headwearColor'] as String?,
  eyewear: $enumDecodeNullable(_$AvatarEyewearEnumMap, json['eyewear']),
  eyewearColor: json['eyewearColor'] as String?,
  earAccessory: $enumDecodeNullable(
    _$AvatarEarAccessoryEnumMap,
    json['earAccessory'],
  ),
  earAccessoryColor: json['earAccessoryColor'] as String?,
  neckAccessory: $enumDecodeNullable(
    _$AvatarNeckAccessoryEnumMap,
    json['neckAccessory'],
  ),
  neckAccessoryColor: json['neckAccessoryColor'] as String?,
  handAccessory: $enumDecodeNullable(
    _$AvatarHandAccessoryEnumMap,
    json['handAccessory'],
  ),
  handAccessoryColor: json['handAccessoryColor'] as String?,
  backAccessory: $enumDecodeNullable(
    _$AvatarBackAccessoryEnumMap,
    json['backAccessory'],
  ),
  backAccessoryColor: json['backAccessoryColor'] as String?,
  layers:
      (json['layers'] as List<dynamic>?)
          ?.map((e) => AvatarLayer.fromJson(e as Map<String, dynamic>))
          .toList() ??
      const [],
  prop: $enumDecodeNullable(_$AvatarPropEnumMap, json['prop']),
  background:
      $enumDecodeNullable(_$AvatarBackgroundEnumMap, json['background']) ??
      AvatarBackground.solid,
  backgroundCustomColor: json['backgroundCustomColor'] as String?,
  aura: $enumDecodeNullable(_$AvatarAuraEnumMap, json['aura']),
  auraColor: json['auraColor'] as String?,
  createdAt: DateTime.parse(json['createdAt'] as String),
  updatedAt: json['updatedAt'] == null
      ? null
      : DateTime.parse(json['updatedAt'] as String),
  isPremium: json['isPremium'] as bool? ?? false,
  unlockState:
      $enumDecodeNullable(_$AvatarUnlockStateEnumMap, json['unlockState']) ??
      AvatarUnlockState.unlocked,
  tags:
      (json['tags'] as List<dynamic>?)?.map((e) => e as String).toList() ??
      const [],
);

Map<String, dynamic> _$FullAvatarToJson(FullAvatar instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'schemaVersion': instance.schemaVersion,
      'bodyMetrics': instance.bodyMetrics.toJson(),
      'bodyType': _$AvatarBodyTypeEnumMap[instance.bodyType]!,
      'height': instance.height,
      'skinTone': instance.skinTone,
      'headShape': _$AvatarHeadShapeEnumMap[instance.headShape]!,
      'faceShape': _$AvatarFaceShapeEnumMap[instance.faceShape]!,
      'hairStyle': _$AvatarHairStyle2EnumMap[instance.hairStyle]!,
      'hairColor': instance.hairColor,
      'hairHighlights': instance.hairHighlights,
      'highlightColor': instance.highlightColor,
      'eyebrowStyle': _$AvatarEyebrowStyleEnumMap[instance.eyebrowStyle]!,
      'eyebrowColor': instance.eyebrowColor,
      'eyebrowPosition': instance.eyebrowPosition,
      'eyeShape': _$AvatarEyeShapeEnumMap[instance.eyeShape]!,
      'eyeColor': instance.eyeColor,
      'eyeSize': instance.eyeSize,
      'eyeSpacing': instance.eyeSpacing,
      'eyelashStyle': _$AvatarEyelashStyleEnumMap[instance.eyelashStyle]!,
      'noseStyle': _$AvatarNoseStyleEnumMap[instance.noseStyle]!,
      'noseSize': instance.noseSize,
      'mouthShape': _$AvatarMouthShapeEnumMap[instance.mouthShape]!,
      'lipColor': instance.lipColor,
      'mouthSize': instance.mouthSize,
      'facialHair': _$AvatarFacialHair2EnumMap[instance.facialHair],
      'facialHairColor': instance.facialHairColor,
      'facialFeatures': instance.facialFeatures
          .map((e) => _$AvatarFacialFeatureEnumMap[e]!)
          .toList(),
      'blushIntensity': instance.blushIntensity,
      'expression': _$AvatarExpressionEnumMap[instance.expression]!,
      'pose': _$AvatarPoseEnumMap[instance.pose]!,
      'animationProfile':
          _$AvatarAnimationProfileEnumMap[instance.animationProfile]!,
      'topStyle': _$AvatarTopStyleEnumMap[instance.topStyle]!,
      'topPrimaryColor': instance.topPrimaryColor,
      'topSecondaryColor': instance.topSecondaryColor,
      'topPattern': _$AvatarPatternEnumMap[instance.topPattern],
      'bottomStyle': _$AvatarBottomStyleEnumMap[instance.bottomStyle]!,
      'bottomColor': instance.bottomColor,
      'bottomPattern': _$AvatarPatternEnumMap[instance.bottomPattern],
      'outfit': _$AvatarOutfitEnumMap[instance.outfit],
      'outfitColor': instance.outfitColor,
      'footwear': _$AvatarFootwearEnumMap[instance.footwear]!,
      'footwearColor': instance.footwearColor,
      'headwear': _$AvatarHeadwearEnumMap[instance.headwear],
      'headwearColor': instance.headwearColor,
      'eyewear': _$AvatarEyewearEnumMap[instance.eyewear],
      'eyewearColor': instance.eyewearColor,
      'earAccessory': _$AvatarEarAccessoryEnumMap[instance.earAccessory],
      'earAccessoryColor': instance.earAccessoryColor,
      'neckAccessory': _$AvatarNeckAccessoryEnumMap[instance.neckAccessory],
      'neckAccessoryColor': instance.neckAccessoryColor,
      'handAccessory': _$AvatarHandAccessoryEnumMap[instance.handAccessory],
      'handAccessoryColor': instance.handAccessoryColor,
      'backAccessory': _$AvatarBackAccessoryEnumMap[instance.backAccessory],
      'backAccessoryColor': instance.backAccessoryColor,
      'layers': instance.layers.map((e) => e.toJson()).toList(),
      'prop': _$AvatarPropEnumMap[instance.prop],
      'background': _$AvatarBackgroundEnumMap[instance.background]!,
      'backgroundCustomColor': instance.backgroundCustomColor,
      'aura': _$AvatarAuraEnumMap[instance.aura],
      'auraColor': instance.auraColor,
      'createdAt': instance.createdAt.toIso8601String(),
      'updatedAt': instance.updatedAt?.toIso8601String(),
      'isPremium': instance.isPremium,
      'unlockState': _$AvatarUnlockStateEnumMap[instance.unlockState]!,
      'tags': instance.tags,
    };

const _$AvatarBodyTypeEnumMap = {
  AvatarBodyType.slim: 'slim',
  AvatarBodyType.average: 'average',
  AvatarBodyType.athletic: 'athletic',
  AvatarBodyType.curvy: 'curvy',
  AvatarBodyType.broad: 'broad',
};

const _$AvatarHeadShapeEnumMap = {
  AvatarHeadShape.round: 'round',
  AvatarHeadShape.oval: 'oval',
  AvatarHeadShape.square: 'square',
  AvatarHeadShape.heart: 'heart',
  AvatarHeadShape.oblong: 'oblong',
  AvatarHeadShape.diamond: 'diamond',
};

const _$AvatarFaceShapeEnumMap = {
  AvatarFaceShape.round: 'round',
  AvatarFaceShape.oval: 'oval',
  AvatarFaceShape.angular: 'angular',
  AvatarFaceShape.soft: 'soft',
};

const _$AvatarHairStyle2EnumMap = {
  AvatarHairStyle2.bald: 'bald',
  AvatarHairStyle2.buzzCut: 'buzzCut',
  AvatarHairStyle2.crewCut: 'crewCut',
  AvatarHairStyle2.short: 'short',
  AvatarHairStyle2.medium: 'medium',
  AvatarHairStyle2.long: 'long',
  AvatarHairStyle2.veryLong: 'veryLong',
  AvatarHairStyle2.pixie: 'pixie',
  AvatarHairStyle2.bob: 'bob',
  AvatarHairStyle2.lob: 'lob',
  AvatarHairStyle2.shag: 'shag',
  AvatarHairStyle2.layers: 'layers',
  AvatarHairStyle2.bangs: 'bangs',
  AvatarHairStyle2.sidePart: 'sidePart',
  AvatarHairStyle2.middlePart: 'middlePart',
  AvatarHairStyle2.slickedBack: 'slickedBack',
  AvatarHairStyle2.pompadour: 'pompadour',
  AvatarHairStyle2.undercut: 'undercut',
  AvatarHairStyle2.mohawk: 'mohawk',
  AvatarHairStyle2.fauxHawk: 'fauxHawk',
  AvatarHairStyle2.spiky: 'spiky',
  AvatarHairStyle2.messy: 'messy',
  AvatarHairStyle2.curlyShort: 'curlyShort',
  AvatarHairStyle2.curlyMedium: 'curlyMedium',
  AvatarHairStyle2.curlyLong: 'curlyLong',
  AvatarHairStyle2.wavyShort: 'wavyShort',
  AvatarHairStyle2.wavyMedium: 'wavyMedium',
  AvatarHairStyle2.wavyLong: 'wavyLong',
  AvatarHairStyle2.afro: 'afro',
  AvatarHairStyle2.afroShort: 'afroShort',
  AvatarHairStyle2.cornrows: 'cornrows',
  AvatarHairStyle2.braids: 'braids',
  AvatarHairStyle2.boxBraids: 'boxBraids',
  AvatarHairStyle2.dreadlocks: 'dreadlocks',
  AvatarHairStyle2.twists: 'twists',
  AvatarHairStyle2.ponytail: 'ponytail',
  AvatarHairStyle2.highPonytail: 'highPonytail',
  AvatarHairStyle2.lowPonytail: 'lowPonytail',
  AvatarHairStyle2.sidePonytail: 'sidePonytail',
  AvatarHairStyle2.pigtails: 'pigtails',
  AvatarHairStyle2.spaceBuns: 'spaceBuns',
  AvatarHairStyle2.bun: 'bun',
  AvatarHairStyle2.topKnot: 'topKnot',
  AvatarHairStyle2.messyBun: 'messyBun',
  AvatarHairStyle2.braidedBun: 'braidedBun',
  AvatarHairStyle2.halfUp: 'halfUp',
  AvatarHairStyle2.halfUpBun: 'halfUpBun',
};

const _$AvatarEyebrowStyleEnumMap = {
  AvatarEyebrowStyle.natural: 'natural',
  AvatarEyebrowStyle.thick: 'thick',
  AvatarEyebrowStyle.thin: 'thin',
  AvatarEyebrowStyle.arched: 'arched',
  AvatarEyebrowStyle.straight: 'straight',
  AvatarEyebrowStyle.curved: 'curved',
  AvatarEyebrowStyle.angular: 'angular',
  AvatarEyebrowStyle.bushy: 'bushy',
  AvatarEyebrowStyle.feathered: 'feathered',
  AvatarEyebrowStyle.none: 'none',
};

const _$AvatarEyeShapeEnumMap = {
  AvatarEyeShape.almond: 'almond',
  AvatarEyeShape.round: 'round',
  AvatarEyeShape.hooded: 'hooded',
  AvatarEyeShape.monolid: 'monolid',
  AvatarEyeShape.downturned: 'downturned',
  AvatarEyeShape.upturned: 'upturned',
  AvatarEyeShape.wide: 'wide',
  AvatarEyeShape.narrow: 'narrow',
  AvatarEyeShape.deepSet: 'deepSet',
};

const _$AvatarEyelashStyleEnumMap = {
  AvatarEyelashStyle.none: 'none',
  AvatarEyelashStyle.natural: 'natural',
  AvatarEyelashStyle.long: 'long',
  AvatarEyelashStyle.dramatic: 'dramatic',
  AvatarEyelashStyle.wispy: 'wispy',
  AvatarEyelashStyle.doll: 'doll',
};

const _$AvatarNoseStyleEnumMap = {
  AvatarNoseStyle.small: 'small',
  AvatarNoseStyle.medium: 'medium',
  AvatarNoseStyle.large: 'large',
  AvatarNoseStyle.button: 'button',
  AvatarNoseStyle.pointed: 'pointed',
  AvatarNoseStyle.rounded: 'rounded',
  AvatarNoseStyle.wide: 'wide',
  AvatarNoseStyle.narrow: 'narrow',
  AvatarNoseStyle.roman: 'roman',
  AvatarNoseStyle.snub: 'snub',
};

const _$AvatarMouthShapeEnumMap = {
  AvatarMouthShape.natural: 'natural',
  AvatarMouthShape.full: 'full',
  AvatarMouthShape.thin: 'thin',
  AvatarMouthShape.wide: 'wide',
  AvatarMouthShape.small: 'small',
  AvatarMouthShape.hearted: 'hearted',
  AvatarMouthShape.downturned: 'downturned',
  AvatarMouthShape.upturned: 'upturned',
};

const _$AvatarFacialHair2EnumMap = {
  AvatarFacialHair2.none: 'none',
  AvatarFacialHair2.stubble: 'stubble',
  AvatarFacialHair2.goatee: 'goatee',
  AvatarFacialHair2.soulPatch: 'soulPatch',
  AvatarFacialHair2.mustache: 'mustache',
  AvatarFacialHair2.handlebar: 'handlebar',
  AvatarFacialHair2.beardShort: 'beardShort',
  AvatarFacialHair2.beardMedium: 'beardMedium',
  AvatarFacialHair2.beardLong: 'beardLong',
  AvatarFacialHair2.beardFull: 'beardFull',
  AvatarFacialHair2.mutton: 'mutton',
  AvatarFacialHair2.vandyke: 'vandyke',
};

const _$AvatarFacialFeatureEnumMap = {
  AvatarFacialFeature.freckles: 'freckles',
  AvatarFacialFeature.frecklesLight: 'frecklesLight',
  AvatarFacialFeature.moleLeft: 'moleLeft',
  AvatarFacialFeature.moleRight: 'moleRight',
  AvatarFacialFeature.beautyMark: 'beautyMark',
  AvatarFacialFeature.dimples: 'dimples',
  AvatarFacialFeature.scarLeft: 'scarLeft',
  AvatarFacialFeature.scarRight: 'scarRight',
  AvatarFacialFeature.scarChin: 'scarChin',
  AvatarFacialFeature.birthmark: 'birthmark',
  AvatarFacialFeature.wrinkles: 'wrinkles',
  AvatarFacialFeature.crowsFeet: 'crowsFeet',
};

const _$AvatarExpressionEnumMap = {
  AvatarExpression.neutral: 'neutral',
  AvatarExpression.happy: 'happy',
  AvatarExpression.excited: 'excited',
  AvatarExpression.laughing: 'laughing',
  AvatarExpression.wink: 'wink',
  AvatarExpression.smirk: 'smirk',
  AvatarExpression.confident: 'confident',
  AvatarExpression.cool: 'cool',
  AvatarExpression.surprised: 'surprised',
  AvatarExpression.shocked: 'shocked',
  AvatarExpression.thinking: 'thinking',
  AvatarExpression.confused: 'confused',
  AvatarExpression.sad: 'sad',
  AvatarExpression.angry: 'angry',
  AvatarExpression.determined: 'determined',
  AvatarExpression.sleepy: 'sleepy',
  AvatarExpression.silly: 'silly',
  AvatarExpression.love: 'love',
  AvatarExpression.starryEyed: 'starryEyed',
  AvatarExpression.crying: 'crying',
};

const _$AvatarPoseEnumMap = {
  AvatarPose.standing: 'standing',
  AvatarPose.standingRelaxed: 'standingRelaxed',
  AvatarPose.handOnHip: 'handOnHip',
  AvatarPose.handsOnHips: 'handsOnHips',
  AvatarPose.armsCrossed: 'armsCrossed',
  AvatarPose.waving: 'waving',
  AvatarPose.peace: 'peace',
  AvatarPose.thumbsUp: 'thumbsUp',
  AvatarPose.pointing: 'pointing',
  AvatarPose.thinking: 'thinking',
  AvatarPose.shrug: 'shrug',
  AvatarPose.celebration: 'celebration',
  AvatarPose.jumping: 'jumping',
  AvatarPose.sitting: 'sitting',
  AvatarPose.lounging: 'lounging',
  AvatarPose.dancing: 'dancing',
  AvatarPose.actionPose: 'actionPose',
  AvatarPose.heroic: 'heroic',
};

const _$AvatarAnimationProfileEnumMap = {
  AvatarAnimationProfile.idle: 'idle',
  AvatarAnimationProfile.breathing: 'breathing',
  AvatarAnimationProfile.waving: 'waving',
  AvatarAnimationProfile.dancing: 'dancing',
  AvatarAnimationProfile.running: 'running',
  AvatarAnimationProfile.sitting: 'sitting',
  AvatarAnimationProfile.floating: 'floating',
  AvatarAnimationProfile.custom: 'custom',
};

const _$AvatarTopStyleEnumMap = {
  AvatarTopStyle.none: 'none',
  AvatarTopStyle.tshirt: 'tshirt',
  AvatarTopStyle.tshirtVneck: 'tshirtVneck',
  AvatarTopStyle.tankTop: 'tankTop',
  AvatarTopStyle.cropTop: 'cropTop',
  AvatarTopStyle.polo: 'polo',
  AvatarTopStyle.buttonUp: 'buttonUp',
  AvatarTopStyle.blouse: 'blouse',
  AvatarTopStyle.sweater: 'sweater',
  AvatarTopStyle.hoodie: 'hoodie',
  AvatarTopStyle.hoodieZip: 'hoodieZip',
  AvatarTopStyle.cardigan: 'cardigan',
  AvatarTopStyle.jacket: 'jacket',
  AvatarTopStyle.leatherJacket: 'leatherJacket',
  AvatarTopStyle.denimJacket: 'denimJacket',
  AvatarTopStyle.blazer: 'blazer',
  AvatarTopStyle.vest: 'vest',
  AvatarTopStyle.turtleneck: 'turtleneck',
  AvatarTopStyle.offShoulder: 'offShoulder',
  AvatarTopStyle.longSleeve: 'longSleeve',
  AvatarTopStyle.jerseyAthletic: 'jerseyAthletic',
  AvatarTopStyle.jerseyBaseball: 'jerseyBaseball',
};

const _$AvatarPatternEnumMap = {
  AvatarPattern.solid: 'solid',
  AvatarPattern.stripes: 'stripes',
  AvatarPattern.stripesHorizontal: 'stripesHorizontal',
  AvatarPattern.plaid: 'plaid',
  AvatarPattern.polkaDots: 'polkaDots',
  AvatarPattern.floral: 'floral',
  AvatarPattern.geometric: 'geometric',
  AvatarPattern.camo: 'camo',
  AvatarPattern.tieDye: 'tieDye',
  AvatarPattern.gradient: 'gradient',
  AvatarPattern.colorBlock: 'colorBlock',
  AvatarPattern.graphic: 'graphic',
  AvatarPattern.logoSmall: 'logoSmall',
  AvatarPattern.logoLarge: 'logoLarge',
};

const _$AvatarBottomStyleEnumMap = {
  AvatarBottomStyle.jeans: 'jeans',
  AvatarBottomStyle.jeansSkinny: 'jeansSkinny',
  AvatarBottomStyle.jeansWide: 'jeansWide',
  AvatarBottomStyle.jeansShorts: 'jeansShorts',
  AvatarBottomStyle.chinos: 'chinos',
  AvatarBottomStyle.slacks: 'slacks',
  AvatarBottomStyle.shorts: 'shorts',
  AvatarBottomStyle.cargoShorts: 'cargoShorts',
  AvatarBottomStyle.cargoPants: 'cargoPants',
  AvatarBottomStyle.joggers: 'joggers',
  AvatarBottomStyle.sweatpants: 'sweatpants',
  AvatarBottomStyle.leggings: 'leggings',
  AvatarBottomStyle.skirtMini: 'skirtMini',
  AvatarBottomStyle.skirtMidi: 'skirtMidi',
  AvatarBottomStyle.skirtMaxi: 'skirtMaxi',
  AvatarBottomStyle.skirtPleated: 'skirtPleated',
};

const _$AvatarOutfitEnumMap = {
  AvatarOutfit.dress: 'dress',
  AvatarOutfit.dressLong: 'dressLong',
  AvatarOutfit.dressCocktail: 'dressCocktail',
  AvatarOutfit.jumpsuit: 'jumpsuit',
  AvatarOutfit.romper: 'romper',
  AvatarOutfit.overalls: 'overalls',
  AvatarOutfit.suit: 'suit',
  AvatarOutfit.suitCasual: 'suitCasual',
  AvatarOutfit.uniform: 'uniform',
  AvatarOutfit.athletic: 'athletic',
  AvatarOutfit.swimsuit: 'swimsuit',
  AvatarOutfit.pajamas: 'pajamas',
  AvatarOutfit.onesie: 'onesie',
  AvatarOutfit.costumeSuperhero: 'costumeSuperhero',
  AvatarOutfit.costumePrincess: 'costumePrincess',
  AvatarOutfit.costumePirate: 'costumePirate',
  AvatarOutfit.costumeNinja: 'costumeNinja',
  AvatarOutfit.costumeAstronaut: 'costumeAstronaut',
};

const _$AvatarFootwearEnumMap = {
  AvatarFootwear.barefoot: 'barefoot',
  AvatarFootwear.sneakers: 'sneakers',
  AvatarFootwear.sneakersHighTop: 'sneakersHighTop',
  AvatarFootwear.runningShoes: 'runningShoes',
  AvatarFootwear.loafers: 'loafers',
  AvatarFootwear.oxfords: 'oxfords',
  AvatarFootwear.boots: 'boots',
  AvatarFootwear.bootsAnkle: 'bootsAnkle',
  AvatarFootwear.bootsKnee: 'bootsKnee',
  AvatarFootwear.heels: 'heels',
  AvatarFootwear.heelsHigh: 'heelsHigh',
  AvatarFootwear.wedges: 'wedges',
  AvatarFootwear.sandals: 'sandals',
  AvatarFootwear.flipFlops: 'flipFlops',
  AvatarFootwear.slippers: 'slippers',
};

const _$AvatarHeadwearEnumMap = {
  AvatarHeadwear.none: 'none',
  AvatarHeadwear.cap: 'cap',
  AvatarHeadwear.capBackward: 'capBackward',
  AvatarHeadwear.beanie: 'beanie',
  AvatarHeadwear.fedora: 'fedora',
  AvatarHeadwear.sunHat: 'sunHat',
  AvatarHeadwear.bucket: 'bucket',
  AvatarHeadwear.beret: 'beret',
  AvatarHeadwear.headband: 'headband',
  AvatarHeadwear.headbandAthletic: 'headbandAthletic',
  AvatarHeadwear.bandana: 'bandana',
  AvatarHeadwear.bow: 'bow',
  AvatarHeadwear.bowLarge: 'bowLarge',
  AvatarHeadwear.hairClips: 'hairClips',
  AvatarHeadwear.crown: 'crown',
  AvatarHeadwear.tiara: 'tiara',
  AvatarHeadwear.partyHat: 'partyHat',
  AvatarHeadwear.antennaBoppers: 'antennaBoppers',
  AvatarHeadwear.catEars: 'catEars',
  AvatarHeadwear.bunnyEars: 'bunnyEars',
  AvatarHeadwear.devilHorns: 'devilHorns',
  AvatarHeadwear.halo: 'halo',
  AvatarHeadwear.flowers: 'flowers',
  AvatarHeadwear.helmet: 'helmet',
};

const _$AvatarEyewearEnumMap = {
  AvatarEyewear.none: 'none',
  AvatarEyewear.glasses: 'glasses',
  AvatarEyewear.glassesRound: 'glassesRound',
  AvatarEyewear.glassesSquare: 'glassesSquare',
  AvatarEyewear.glassesCatEye: 'glassesCatEye',
  AvatarEyewear.glassesAviator: 'glassesAviator',
  AvatarEyewear.sunglasses: 'sunglasses',
  AvatarEyewear.sunglassesAviator: 'sunglassesAviator',
  AvatarEyewear.sunglassesSport: 'sunglassesSport',
  AvatarEyewear.sunglassesOversized: 'sunglassesOversized',
  AvatarEyewear.monocle: 'monocle',
  AvatarEyewear.eyepatch: 'eyepatch',
  AvatarEyewear.mask: 'mask',
  AvatarEyewear.maskMasquerade: 'maskMasquerade',
};

const _$AvatarEarAccessoryEnumMap = {
  AvatarEarAccessory.none: 'none',
  AvatarEarAccessory.studsSmall: 'studsSmall',
  AvatarEarAccessory.studsMedium: 'studsMedium',
  AvatarEarAccessory.hoopsSmall: 'hoopsSmall',
  AvatarEarAccessory.hoopsMedium: 'hoopsMedium',
  AvatarEarAccessory.hoopsLarge: 'hoopsLarge',
  AvatarEarAccessory.dangles: 'dangles',
  AvatarEarAccessory.cuffs: 'cuffs',
  AvatarEarAccessory.plugs: 'plugs',
  AvatarEarAccessory.clipOns: 'clipOns',
};

const _$AvatarNeckAccessoryEnumMap = {
  AvatarNeckAccessory.none: 'none',
  AvatarNeckAccessory.necklaceSimple: 'necklaceSimple',
  AvatarNeckAccessory.necklaceChain: 'necklaceChain',
  AvatarNeckAccessory.necklaceChunky: 'necklaceChunky',
  AvatarNeckAccessory.necklacePendant: 'necklacePendant',
  AvatarNeckAccessory.necklaceChoker: 'necklaceChoker',
  AvatarNeckAccessory.tie: 'tie',
  AvatarNeckAccessory.tieSkinny: 'tieSkinny',
  AvatarNeckAccessory.bowtie: 'bowtie',
  AvatarNeckAccessory.scarf: 'scarf',
  AvatarNeckAccessory.scarfWinter: 'scarfWinter',
  AvatarNeckAccessory.bandana: 'bandana',
  AvatarNeckAccessory.headphones: 'headphones',
};

const _$AvatarHandAccessoryEnumMap = {
  AvatarHandAccessory.none: 'none',
  AvatarHandAccessory.watch: 'watch',
  AvatarHandAccessory.watchSmart: 'watchSmart',
  AvatarHandAccessory.bracelet: 'bracelet',
  AvatarHandAccessory.bracelets: 'bracelets',
  AvatarHandAccessory.bangles: 'bangles',
  AvatarHandAccessory.ring: 'ring',
  AvatarHandAccessory.rings: 'rings',
  AvatarHandAccessory.gloves: 'gloves',
  AvatarHandAccessory.wristband: 'wristband',
};

const _$AvatarBackAccessoryEnumMap = {
  AvatarBackAccessory.none: 'none',
  AvatarBackAccessory.backpack: 'backpack',
  AvatarBackAccessory.backpackSmall: 'backpackSmall',
  AvatarBackAccessory.messenger: 'messenger',
  AvatarBackAccessory.purse: 'purse',
  AvatarBackAccessory.duffel: 'duffel',
  AvatarBackAccessory.wings: 'wings',
  AvatarBackAccessory.wingsAngel: 'wingsAngel',
  AvatarBackAccessory.wingsBat: 'wingsBat',
  AvatarBackAccessory.wingsFairy: 'wingsFairy',
  AvatarBackAccessory.cape: 'cape',
  AvatarBackAccessory.capeSuperHero: 'capeSuperHero',
};

const _$AvatarPropEnumMap = {
  AvatarProp.none: 'none',
  AvatarProp.phone: 'phone',
  AvatarProp.tablet: 'tablet',
  AvatarProp.laptop: 'laptop',
  AvatarProp.book: 'book',
  AvatarProp.coffee: 'coffee',
  AvatarProp.soda: 'soda',
  AvatarProp.balloon: 'balloon',
  AvatarProp.balloons: 'balloons',
  AvatarProp.flower: 'flower',
  AvatarProp.bouquet: 'bouquet',
  AvatarProp.flag: 'flag',
  AvatarProp.sign: 'sign',
  AvatarProp.sword: 'sword',
  AvatarProp.shield: 'shield',
  AvatarProp.wand: 'wand',
  AvatarProp.staff: 'staff',
  AvatarProp.guitar: 'guitar',
  AvatarProp.microphone: 'microphone',
  AvatarProp.basketball: 'basketball',
  AvatarProp.football: 'football',
  AvatarProp.soccer: 'soccer',
  AvatarProp.baseball: 'baseball',
  AvatarProp.skateboard: 'skateboard',
  AvatarProp.camera: 'camera',
  AvatarProp.paintbrush: 'paintbrush',
  AvatarProp.pencil: 'pencil',
  AvatarProp.gameController: 'gameController',
  AvatarProp.trophy: 'trophy',
  AvatarProp.medal: 'medal',
  AvatarProp.heart: 'heart',
  AvatarProp.star: 'star',
  AvatarProp.sparkler: 'sparkler',
  AvatarProp.umbrella: 'umbrella',
  AvatarProp.petDog: 'petDog',
  AvatarProp.petCat: 'petCat',
  AvatarProp.petBird: 'petBird',
  AvatarProp.petFish: 'petFish',
};

const _$AvatarBackgroundEnumMap = {
  AvatarBackground.none: 'none',
  AvatarBackground.solid: 'solid',
  AvatarBackground.gradient: 'gradient',
  AvatarBackground.pattern: 'pattern',
  AvatarBackground.sceneBeach: 'sceneBeach',
  AvatarBackground.sceneMountains: 'sceneMountains',
  AvatarBackground.sceneCity: 'sceneCity',
  AvatarBackground.scenePark: 'scenePark',
  AvatarBackground.sceneSpace: 'sceneSpace',
  AvatarBackground.sceneClouds: 'sceneClouds',
  AvatarBackground.sceneRainbow: 'sceneRainbow',
  AvatarBackground.sceneStars: 'sceneStars',
  AvatarBackground.sceneHearts: 'sceneHearts',
  AvatarBackground.sceneConfetti: 'sceneConfetti',
  AvatarBackground.sceneGaming: 'sceneGaming',
  AvatarBackground.sceneSports: 'sceneSports',
  AvatarBackground.sceneMusic: 'sceneMusic',
  AvatarBackground.sceneArt: 'sceneArt',
};

const _$AvatarAuraEnumMap = {
  AvatarAura.none: 'none',
  AvatarAura.glow: 'glow',
  AvatarAura.sparkle: 'sparkle',
  AvatarAura.flames: 'flames',
  AvatarAura.lightning: 'lightning',
  AvatarAura.bubbles: 'bubbles',
  AvatarAura.hearts: 'hearts',
  AvatarAura.stars: 'stars',
  AvatarAura.music: 'music',
  AvatarAura.rainbow: 'rainbow',
  AvatarAura.cosmic: 'cosmic',
  AvatarAura.nature: 'nature',
};

const _$AvatarUnlockStateEnumMap = {
  AvatarUnlockState.unlocked: 'unlocked',
  AvatarUnlockState.locked: 'locked',
  AvatarUnlockState.premium: 'premium',
  AvatarUnlockState.eventExclusive: 'eventExclusive',
};

AvatarBodyMetrics _$AvatarBodyMetricsFromJson(Map<String, dynamic> json) =>
    AvatarBodyMetrics(
      shoulderWidth: (json['shoulderWidth'] as num?)?.toDouble() ?? 0.5,
      torsoLength: (json['torsoLength'] as num?)?.toDouble() ?? 0.5,
      armLength: (json['armLength'] as num?)?.toDouble() ?? 0.5,
      legLength: (json['legLength'] as num?)?.toDouble() ?? 0.5,
      waistWidth: (json['waistWidth'] as num?)?.toDouble() ?? 0.5,
      hipWidth: (json['hipWidth'] as num?)?.toDouble() ?? 0.5,
      muscleTone: (json['muscleTone'] as num?)?.toDouble() ?? 0.5,
      bodyFat: (json['bodyFat'] as num?)?.toDouble() ?? 0.5,
    );

Map<String, dynamic> _$AvatarBodyMetricsToJson(AvatarBodyMetrics instance) =>
    <String, dynamic>{
      'shoulderWidth': instance.shoulderWidth,
      'torsoLength': instance.torsoLength,
      'armLength': instance.armLength,
      'legLength': instance.legLength,
      'waistWidth': instance.waistWidth,
      'hipWidth': instance.hipWidth,
      'muscleTone': instance.muscleTone,
      'bodyFat': instance.bodyFat,
    };

AvatarLayer _$AvatarLayerFromJson(Map<String, dynamic> json) => AvatarLayer(
  type: $enumDecode(_$AvatarLayerTypeEnumMap, json['type']),
  assetId: json['assetId'] as String,
  zIndex: (json['zIndex'] as num?)?.toInt() ?? 0,
  opacity: (json['opacity'] as num?)?.toDouble() ?? 1,
  isTintable: json['isTintable'] as bool? ?? false,
  tintColor: json['tintColor'] as String?,
);

Map<String, dynamic> _$AvatarLayerToJson(AvatarLayer instance) =>
    <String, dynamic>{
      'type': _$AvatarLayerTypeEnumMap[instance.type]!,
      'assetId': instance.assetId,
      'zIndex': instance.zIndex,
      'opacity': instance.opacity,
      'isTintable': instance.isTintable,
      'tintColor': instance.tintColor,
    };

const _$AvatarLayerTypeEnumMap = {
  AvatarLayerType.base: 'base',
  AvatarLayerType.body: 'body',
  AvatarLayerType.face: 'face',
  AvatarLayerType.hair: 'hair',
  AvatarLayerType.clothing: 'clothing',
  AvatarLayerType.accessory: 'accessory',
  AvatarLayerType.prop: 'prop',
  AvatarLayerType.effect: 'effect',
  AvatarLayerType.background: 'background',
};
