Add-Type -AssemblyName System.Drawing

$src='C:\Users\bkyil\AndroidStudioProjects\NeuroComet\tmp_icon_download\drive_icons_new.bin'
$out='C:\Users\bkyil\AndroidStudioProjects\NeuroComet\tmp_icon_download\drive_icons_new_rows.txt'
$fs=[System.IO.File]::Open($src,[System.IO.FileMode]::Open,[System.IO.FileAccess]::Read,[System.IO.FileShare]::ReadWrite)
$bmp=[System.Drawing.Bitmap]::FromStream($fs,$false,$false)
try {
  $w=200
  $h=[Math]::Round($bmp.Height*$w/$bmp.Width)
  $thumb=New-Object System.Drawing.Bitmap($w,$h)
  $g=[System.Drawing.Graphics]::FromImage($thumb)
  $g.InterpolationMode=[System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
  $g.DrawImage($bmp,0,0,$w,$h)
  $g.Dispose()
  $rows=@()
  for($y=0;$y -lt $h;$y++){
    $sum=0.0
    for($x=0;$x -lt $w;$x++){
      $c=$thumb.GetPixel($x,$y)
      $sum += (0.2126*$c.R+0.7152*$c.G+0.0722*$c.B)
    }
    $avg=[int]($sum/$w)
    $rows += $avg
  }
  $lines=New-Object System.Collections.Generic.List[string]
  $lines.Add("thumb=$w x $h")
  $lines.Add(($rows -join ','))
  Set-Content -Encoding utf8 $out $lines
} finally { $thumb.Dispose(); $bmp.Dispose(); $fs.Dispose() }
