#!/usr/bin/env python3
"""
Simple script to create placeholder launcher icons.
Run this to generate icons, or replace with your own icons in Android Studio.
"""
import os

# Create a simple 1x1 pixel orange PNG for placeholder launcher icons
# This is a minimal valid PNG with orange color (#FF6B35)
png_bytes = bytes([
    0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,  # PNG signature
    0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,  # IHDR chunk
    0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,  # 1x1 pixels
    0x08, 0x02, 0x00, 0x00, 0x00, 0x90, 0x77, 0x53,
    0xDE, 0x00, 0x00, 0x00, 0x0C, 0x49, 0x44, 0x41,  # IDAT chunk
    0x54, 0x08, 0xD7, 0x63, 0xF8, 0xCF, 0xC0, 0x00,
    0x00, 0x00, 0x02, 0x00, 0x01, 0xE2, 0x21, 0xBC,
    0x33, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E,  # IEND chunk
    0x44, 0xAE, 0x42, 0x60, 0x82
])

mipmap_dirs = [
    '/home/claude/NiaziKhataSystem/app/src/main/res/mipmap-hdpi',
    '/home/claude/NiaziKhataSystem/app/src/main/res/mipmap-mdpi',
    '/home/claude/NiaziKhataSystem/app/src/main/res/mipmap-xhdpi',
    '/home/claude/NiaziKhataSystem/app/src/main/res/mipmap-xxhdpi',
    '/home/claude/NiaziKhataSystem/app/src/main/res/mipmap-xxxhdpi',
]

for d in mipmap_dirs:
    os.makedirs(d, exist_ok=True)
    for name in ['ic_launcher.png', 'ic_launcher_round.png']:
        path = os.path.join(d, name)
        with open(path, 'wb') as f:
            f.write(png_bytes)
        print(f"Created: {path}")

print("\nDone! Replace these with proper icons in Android Studio.")
print("Use: Tools > Image Asset Studio to generate proper launcher icons.")
