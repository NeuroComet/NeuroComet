import { mkdir, readFile, writeFile } from 'node:fs/promises';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import { Resvg } from '@resvg/resvg-js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const repoRoot = path.resolve(__dirname, '..', '..');

const renderSize = 512;

const icons = [
  {
    label: 'Calm Waters',
    sourceSvg: path.join(repoRoot, 'app', 'src', 'main', 'res', 'drawable', 'icon_calm_foreground.svg'),
    sharedBaseName: 'calm_waters',
  },
  {
    label: 'Sensory Mode',
    sourceSvg: path.join(repoRoot, 'app', 'src', 'main', 'res', 'drawable', 'icon_sensory_foreground.svg'),
    sharedBaseName: 'sensory_mode',
  },
];

const sharedDir = path.join(repoRoot, 'design', 'assets', 'neuro_states');
const flutterDir = path.join(repoRoot, 'flutter_app', 'assets', 'icons', 'neuro_states');

await mkdir(sharedDir, { recursive: true });
await mkdir(flutterDir, { recursive: true });

const results = [];

for (const icon of icons) {
  const svgContent = await readFile(icon.sourceSvg, 'utf8');
  const sharedSvgPath = path.join(sharedDir, `${icon.sharedBaseName}.svg`);
  const sharedPngPath = path.join(sharedDir, `${icon.sharedBaseName}.png`);
  const flutterPngPath = path.join(flutterDir, `${icon.sharedBaseName}.png`);

  await writeFile(sharedSvgPath, svgContent, 'utf8');

  const resvg = new Resvg(svgContent, {
    fitTo: {
      mode: 'width',
      value: renderSize,
    },
    background: 'rgba(0,0,0,0)',
  });

  const pngBuffer = resvg.render().asPng();
  await writeFile(sharedPngPath, pngBuffer);
  await writeFile(flutterPngPath, pngBuffer);

  results.push({
    label: icon.label,
    sourceSvg: path.relative(repoRoot, icon.sourceSvg).replaceAll('\\', '/'),
    sharedSvg: path.relative(repoRoot, sharedSvgPath).replaceAll('\\', '/'),
    sharedPng: path.relative(repoRoot, sharedPngPath).replaceAll('\\', '/'),
    flutterPng: path.relative(repoRoot, flutterPngPath).replaceAll('\\', '/'),
    pngBytes: pngBuffer.length,
    pngSize: `${renderSize}x${renderSize}`,
  });
}

console.log(JSON.stringify(results, null, 2));

