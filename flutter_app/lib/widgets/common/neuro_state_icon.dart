import 'package:flutter/material.dart';

/// Renders either a branded neuro state asset or a fallback emoji.
class NeuroStateIcon extends StatelessWidget {
  final String emoji;
  final String? assetPath;
  final double size;
  final double contentSize;
  final Color? backgroundColor;

  const NeuroStateIcon({
    super.key,
    required this.emoji,
    this.assetPath,
    this.size = 48,
    this.contentSize = 28,
    this.backgroundColor,
  });

  @override
  Widget build(BuildContext context) {
    final child = assetPath == null
        ? Text(
            emoji,
            style: TextStyle(fontSize: contentSize),
          )
        : Image.asset(
            assetPath!,
            width: contentSize,
            height: contentSize,
            fit: BoxFit.contain,
            errorBuilder: (_, __, ___) => Text(
              emoji,
              style: TextStyle(fontSize: contentSize),
            ),
          );

    if (backgroundColor == null) {
      return SizedBox(
        width: size,
        height: size,
        child: Center(child: child),
      );
    }

    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        color: backgroundColor,
        shape: BoxShape.circle,
      ),
      alignment: Alignment.center,
      child: child,
    );
  }
}

