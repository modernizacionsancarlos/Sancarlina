---
name: Heritage & Harvest
colors:
  surface: '#f9fbea'
  surface-dim: '#d9dbcb'
  surface-bright: '#f9fbea'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f5e4'
  surface-container: '#edefdf'
  surface-container-high: '#e8ead9'
  surface-container-highest: '#e2e4d3'
  on-surface: '#1a1d13'
  on-surface-variant: '#46483b'
  inverse-surface: '#2f3227'
  inverse-on-surface: '#f0f2e1'
  outline: '#76786a'
  outline-variant: '#c6c8b7'
  surface-tint: '#556421'
  primary: '#53621f'
  on-primary: '#ffffff'
  primary-container: '#6b7b36'
  on-primary-container: '#fbffe3'
  inverse-primary: '#bccf7f'
  secondary: '#9f3d4c'
  on-secondary: '#ffffff'
  secondary-container: '#ff8795'
  on-secondary-container: '#771e2f'
  tertiary: '#5b5c5a'
  on-tertiary: '#ffffff'
  tertiary-container: '#737573'
  on-tertiary-container: '#fdfdfa'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#d8eb98'
  primary-fixed-dim: '#bccf7f'
  on-primary-fixed: '#171e00'
  on-primary-fixed-variant: '#3e4c0a'
  secondary-fixed: '#ffdadb'
  secondary-fixed-dim: '#ffb2b9'
  on-secondary-fixed: '#40000f'
  on-secondary-fixed-variant: '#802635'
  tertiary-fixed: '#e2e3e0'
  tertiary-fixed-dim: '#c6c7c4'
  on-tertiary-fixed: '#1a1c1b'
  on-tertiary-fixed-variant: '#454745'
  background: '#f9fbea'
  on-background: '#1a1d13'
  surface-variant: '#e2e4d3'
typography:
  headline-lg:
    fontFamily: Work Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Work Sans
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
    letterSpacing: -0.01em
  headline-sm:
    fontFamily: Work Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Work Sans
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Work Sans
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Work Sans
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.01em
  label-sm:
    fontFamily: Work Sans
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.02em
  headline-lg-mobile:
    fontFamily: Work Sans
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  container-padding: 20px
  gutter: 16px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style
The design system is anchored in a "Regional Professional" aesthetic. It balances the warmth of artisanal craftsmanship with the structured reliability of a premium service. The visual language evokes the organic textures of the earth and the deep hues of matured wine, creating an atmosphere that is grounded, sophisticated, and welcoming.

The style leverages **Minimalism** with **Tactile** accents. It avoids sterile, high-tech gloss in favor of soft matte finishes and generous whitespace, ensuring the interface feels human-centric and accessible to a diverse audience ranging from local producers to modern consumers.

## Colors
The palette is inspired by natural landscapes and traditional harvests. 

- **Primary (Olive Satin):** Used for primary branding, success states, and subtle background surfaces to reinforce the organic theme.
- **Secondary (Deep Wine):** Reserved for high-impact actions, accents, and critical call-to-actions. It provides a sophisticated contrast to the olive tones.
- **Background (Bone White):** The primary canvas for all screens, providing a warm, eye-friendly alternative to pure white.
- **Neutral (Deep Moss):** A darkened, olive-tinted charcoal used for typography and iconography to maintain soft contrast and readability without the harshness of pure black.

## Typography
This design system utilizes **Work Sans** across all levels to maintain a clean, professional, and highly legible environment. 

The hierarchy relies on weight contrast: bold weights (700) are strictly for headlines to provide a confident, structural "skeleton" to the page. Regular weights (400) are used for body copy to ensure comfortable long-form reading, while medium weights (500/600) distinguish labels and secondary metadata. Letter spacing is slightly tightened for large headlines to keep them impactful and loosened for small labels to improve mobile legibility.

## Layout & Spacing
The layout follows a **fluid grid** model optimized for mobile devices, using a 4-column system. 

Horizontal breathing room is prioritized with a 20px outer margin, while internal gutters are kept at 16px to maintain a tight relationship between related items. The vertical rhythm is built on an 8px baseline, ensuring that every element—from the height of a button to the spacing between a headline and a paragraph—is a multiple of 8, creating a sense of professional order and mathematical harmony.

## Elevation & Depth
Depth is conveyed through **Tonal Layers** and very soft **Ambient Shadows**. Instead of high-elevation floating effects, the system uses "shallow" depth to keep the UI feeling grounded and tactile.

- **Level 0 (Base):** Bone White background.
- **Level 1 (Cards):** Slightly elevated using a soft, diffused shadow (Blur: 12px, Y: 4px, Opacity: 4%) with a hint of the Olive primary color in the shadow tint.
- **Level 2 (Modals/Overlays):** Distinctly separated with a 15% backdrop blur to maintain the "satin" feel of the brand.
- **Search Bar:** Uses a clean white surface with a thin 1px border in a light olive-grey to distinguish it from the Bone White background without relying on heavy shadows.

## Shapes
The shape language is friendly and organic. Standard UI components like small buttons or input fields use a 0.5rem (8px) radius. However, the defining characteristic of this design system is the use of **Large Rounded Corners (16px)** for primary containers and cards, which softens the overall look and reinforces the "warm" brand personality. 

Interactive elements like tags or category chips may use pill-shaped (fully rounded) geometry to distinguish them from structural content containers.

## Components

- **Buttons:** Primary buttons are filled with the Wine (#70192A) color, featuring white text for maximum legibility. Secondary buttons use an Olive outline. All buttons should have a minimum height of 48px for touch accessibility.
- **Search Bar:** A clean, high-contrast white field with a 16px corner radius. The placeholder text should be the Neutral Deep Moss at 50% opacity.
- **Cards:** The workhorse of the UI. Use a 16px corner radius, a subtle border-less shadow, and Bone White or pure White fills. Content inside should follow the 16px internal padding rule.
- **Bottom Navigation:** A solid Bone White bar with 2px Wine-colored top border or a very subtle shadow. Icons should be monolinear vector style, using Olive for inactive states and Wine for the active state, accompanied by a small dot indicator.
- **Input Fields:** Rounded corners (8px), white background, and a 1px Olive-grey border that thickens and changes to Wine on focus.
- **Chips/Badges:** Small, 12px-14px text inside fully rounded (pill) containers with light Olive-satin backgrounds.