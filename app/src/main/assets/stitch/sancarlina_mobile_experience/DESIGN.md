---
name: Sancarlina Mobile Experience
colors:
  surface: '#f9f9f6'
  surface-dim: '#dadad7'
  surface-bright: '#f9f9f6'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f4f4f1'
  surface-container: '#eeeeeb'
  surface-container-high: '#e8e8e5'
  surface-container-highest: '#e2e3e0'
  on-surface: '#1a1c1b'
  on-surface-variant: '#444939'
  inverse-surface: '#2f312f'
  inverse-on-surface: '#f1f1ee'
  outline: '#747967'
  outline-variant: '#c4c9b4'
  surface-tint: '#496800'
  primary: '#476500'
  on-primary: '#ffffff'
  primary-container: '#5d7f13'
  on-primary-container: '#faffe7'
  inverse-primary: '#add461'
  secondary: '#a33a47'
  on-secondary: '#ffffff'
  secondary-container: '#fd7f8a'
  on-secondary-container: '#741627'
  tertiary: '#765700'
  on-tertiary: '#ffffff'
  tertiary-container: '#956e00'
  on-tertiary-container: '#fffbff'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c8f17a'
  primary-fixed-dim: '#add461'
  on-primary-fixed: '#131f00'
  on-primary-fixed-variant: '#364e00'
  secondary-fixed: '#ffdadb'
  secondary-fixed-dim: '#ffb2b7'
  on-secondary-fixed: '#40000d'
  on-secondary-fixed-variant: '#842231'
  tertiary-fixed: '#ffdea0'
  tertiary-fixed-dim: '#f6be3b'
  on-tertiary-fixed: '#261900'
  on-tertiary-fixed-variant: '#5c4300'
  background: '#f9f9f6'
  on-background: '#1a1c1b'
  surface-variant: '#e2e3e0'
typography:
  display-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 57px
    fontWeight: '700'
    lineHeight: 64px
    letterSpacing: -0.25px
  headline-lg:
    fontFamily: Be Vietnam Pro
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-md:
    fontFamily: Be Vietnam Pro
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  headline-sm:
    fontFamily: Be Vietnam Pro
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-lg:
    fontFamily: Manrope
    fontSize: 22px
    fontWeight: '500'
    lineHeight: 28px
  body-lg:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
    letterSpacing: 0.5px
  body-md:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
    letterSpacing: 0.25px
  label-lg:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.1px
  headline-lg-mobile:
    fontFamily: Be Vietnam Pro
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 34px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  baseline: 4px
  margin-mobile: 16px
  margin-tablet: 24px
  gutter: 16px
  stack-sm: 8px
  stack-md: 16px
  stack-lg: 24px
---

## Brand & Style

The brand is rooted in the rich, agricultural landscape of its namesake, blending a heritage-inspired color palette with a clean, high-utility Android interface. The target audience values reliability and a "farm-to-table" professional aesthetic—warm yet strictly organized.

The design system adopts a **Corporate Modern** style with **Tactile** influences. It follows Material 3 (M3) specifications rigorously while introducing organic warmth through its color story and hyper-rounded geometry. The visual mood is dependable, premium, and welcoming, avoiding the cold sterility of typical SaaS apps in favor of a sophisticated, earth-toned professionalism.

## Colors

This design system utilizes a palette inspired by viticulture and natural landscapes. 
- **Primary (Olive):** Used for key action buttons, active states in the Navigation Bar, and primary brand iconography.
- **Secondary (Burgundy):** Reserved for accents, notifications, and critical interactive elements that require high visibility against the olive.
- **Background (Cream):** A warm, paper-like neutral that reduces eye strain and distinguishes the app from standard white-label digital products.
- **Surface Tints:** Use M3 color mapping where the surface color is slightly tinted with the primary Olive to create a cohesive tonal environment.

## Typography

The typography strategy pairs the friendly, contemporary curves of **Be Vietnam Pro** for headlines with the technical precision of **Manrope** for functional text. 

Headlines use a tighter tracking and heavier weights to establish a clear hierarchy, while body text is set with generous line heights to ensure readability during long-form consumption. On mobile devices, display and large headline sizes scale down automatically to maintain comfortable margins and prevent excessive line wrapping.

## Layout & Spacing

This design system follows the **Material 3 Fluid Grid** system. On mobile, it utilizes a 4-column grid with 16px side margins. As the screen scales to tablets, it transitions to an 8-column or 12-column grid with 24px margins.

Spacing follows a strict 4dp/8dp incremental rhythm. Elements should be grouped using "stack" units to ensure vertical rhythm is maintained across varied content types. Navigation bars are fixed to the bottom of the screen, while Top App Bars should utilize the "Large" or "Medium" M3 variants to allow the typography to breathe.

## Elevation & Depth

Hierarchy is established using **Tonal Layers** and **Ambient Shadows** in accordance with Material 3’s surface elevation system.

1.  **Level 0 (Base):** The Cream background (#F9F9F6).
2.  **Level 1 (Cards/Lists):** Surface color with a +5% Olive tint and a soft, 4dp blur shadow.
3.  **Level 2 (Navigation/App Bars):** Surface color with +8% Olive tint; no shadow, but distinct tonal separation.
4.  **Level 3 (Modals/Bottom Sheets):** Significant elevation (16dp blur) with a primary-tinted shadow to suggest a physical layer floating above the interface.

Avoid harsh black shadows; use a dark-olive tinted shadow (#2D3615) at 10-15% opacity for a more natural, integrated look.

## Shapes

The shape language is defined by **hyper-roundedness** (24dp corners for major components), reflecting the circular motifs found in the grape and sun icons.

- **Small Components (Chips, Text Fields):** 8dp to 12dp roundedness.
- **Medium Components (Cards, Buttons):** 24dp roundedness.
- **Large Components (Bottom Sheets, Dialogs):** 28dp top-corner roundedness.
- **Iconography:** Map markers and circular action buttons must be perfectly circular (Pill-shaped/Full Round) to accommodate the brand's secondary icon.

## Components

### Buttons
- **Primary:** Olive fill, white text, 24dp rounded corners.
- **Secondary (Burgundy):** Burgundy outline or soft burgundy tint for high-priority secondary actions.
- **FAB:** Circular button using the primary Olive, featuring the mountain/grape icon.

### Cards
- Standard M3 Elevated Cards with 24dp corner radii. Padding should be a minimum of 16dp. Headlines within cards use `Title-Large`.

### Navigation
- **Top App Bar:** Centered logo (the "gondolApp" wordmark) in its original coloring. Use "Medium" height to provide sufficient whitespace.
- **Bottom Navigation Bar:** Use the M3 active-pill indicator in a light Olive tint to highlight the current section.

### Input Fields
- Filled style with a thick bottom stroke in Primary Olive. Corners are rounded 12dp at the top to match the system-wide softness.

### Map Markers & Loading
- Use the mountain/sun/grape circular icon. For map markers, place the circular icon inside a "pin" container with a Burgundy tail to contrast against green map tiles.