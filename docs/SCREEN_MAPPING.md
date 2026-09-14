# Mapeo Stitch → Android

Proyecto Stitch: `18441078248625200523`  
Estado: fuentes preservadas; variantes `FINAL` generadas.

| Vista original de Stitch | ID Stitch | Vista final Stitch | Ruta Android | Archivo Compose | ViewModel/datos | Estado |
|---|---|---|---|---|---|---|
| Splash Screen | `d3d6e7d0f0e9461194e32fa316d03644` | FINAL — Splash (sesión `16621058749917883244`) | `splash` | `ui/features/splash/SplashContent.kt` | `SplashViewModel` | Implementada |
| Onboarding - Descubre | `bcc5bf3505ba4ebe87b831ac1f32a6a8` | FINAL — Onboarding (sesión inicial) | `onboarding` | `ui/features/auth/OnboardingContent.kt` | Preferencias locales | Implementada |
| Login | `5c07b902e8234d65966f1b8c11d00a9e` | FINAL — Login (sesión inicial) | `login` | `ui/features/auth/LoginContent.kt` | `AuthViewModel` / Firebase Auth | Implementada |
| Registro | `1c9aa55b8e3044f8b20caa500990aa29` | FINAL — Registro (sesión inicial) | `register` | `ui/features/auth/RegisterContent.kt` | `AuthViewModel` | Implementada |
| Recuperar Contraseña | `0e0ecf355b414db69dfc95ddc34471b6` | FINAL — Recuperar (sesión inicial) | `forgot_password` | `ui/features/auth/ForgotPasswordContent.kt` | Firebase Auth | Implementada |
| Home Premium Evolution | `15098bc1d15f4074b34670e3d2165470` | Referencia final aprobada | `home` | `ui/features/home/HomeContent.kt` | `HomeViewModel` / comercios reales | Implementada y validada |
| Buscador Avanzado | `a510f3474ac54aa991360a69767f3404` | FINAL — Buscador (sesión inicial) | `search` | `ui/features/home/SearchContent.kt` | `SearchViewModel` | Implementada |
| Filtros Avanzados | `282ba1e99cbd4e3a8519b0cd91f8ddd0` | FINAL — Filtros (sesión inicial) | Integrada en búsqueda/mapa | `SearchContent.kt`, `MapContent.kt` | ViewModels de búsqueda/mapa | Implementada |
| Artesanías | `7b49a2984a2f4874a9c690b19bc596ef` | FINAL — Categoría (sesión inicial) | `category_list/{categoryId}` | `ui/features/category/CategoryListContent.kt` | `CategoryListViewModel` | Implementada |
| Detalle de Producto | `fb95c64bbcc14387b64c4e26d9c86733` | FINAL — Producto (sesión inicial) | `product_detail/{productId}` | `ui/features/product/ProductDetailContent.kt` | `ProductDetailViewModel` | Implementada |
| Galería de Fotos | `ca0a0b2a5c8f4a1895155ac4163cd269` | `b08fd839b2404f17bf1a3fb4f55db14b` | Diálogo de detalle | `product/components/ImageGalleryDialog.kt` | Imágenes del producto | Implementada |
| Mapa Interactivo Modern | `dd7189d1c1c04ceea622b58007181123` | `df592399753f48849fcce97514ba3032` | `map` | `ui/features/map/MapContent.kt` | `MapViewModel`, Maps, ubicación | Implementada |
| Detalle en Mapa | `f0576c29ad384be58fc9c81ad0738ab4` | `cbc7ade52010433d99ec40a2c24782d9` | Bottom sheet de mapa | `map/components/MapTenantBottomSheetCard.kt` | Marker seleccionado | Implementada |
| Turismo | `3532ea1a5c054b779d010c9a77ce38ea` | `2ee2709144e742f49ce29b2b86936386` | `turismo` | `ui/features/turismo/TurismoContent.kt` | `TurismoViewModel` | Implementada |
| Detalle experiencia | `c1c14f0e7f814d7aa57826b43404304f` | `73ca3fd67fe745d3b22af2cd9a1d25d4` | `turismo_detail/{pointId}` | `TurismoDetailContent.kt` | `TurismoDetailViewModel` | Implementada |
| Favoritos | `2a6c729af3264c9097938527929b0059` | `2b69f33f8b724ff5b1b7d341a3c70ee0` | `favorites` | `ui/features/favorites/FavoritesContent.kt` | `FavoritesViewModel` | Implementada |
| Panel de Puntos Modern | `9a0f4285b1654e51b7aadb354c62bf2f` | `0f8cf9d58da7490e89b6f590823feb71` | `points` | `ui/features/points/BenefitsContent.kt` | `PointsViewModel` | Implementada |
| Historial de Puntos | `00936b4999044c8bb1cfef9bd630e47d` | `0c1cf8be77b9401e8211daca092a6ccf` | `points_history` | `PointsHistoryContent.kt` | `PointsHistoryViewModel` | Implementada |
| Escáner QR | `a8f6d748143648cb92c5991d2d650d2e` | `f746b560d4404691af8ae8b1b0bef723` | `qr_scanner` | `QrScannerContent.kt` | `QrScannerViewModel`, cámara | Implementada |
| Perfil Usuario Modern | `a75995caccbc403892d5d05187b0fba4` | `5fe5181eece04db39b0cc112bf26028f` | `profile` | `ui/features/profile/ProfileContent.kt` | `ProfileViewModel` | Implementada |
| Editar Perfil | `aae1e204f8c5417b85c8fff7ccfe2c96` | `856e4c0ec0f4467685d60fe16d45d3dc` | `edit_profile` | `EditProfileContent.kt` | `EditProfileViewModel` | Implementada |
| Notificaciones | `8d2cdeb9d7fc42148f287c379695da29` | `84f659923c2847da9f49afefe56b71f3` | `notifications` | `NotificationsContent.kt` | `NotificationsViewModel` | Implementada |
| Ajustes Notificaciones | `71ad56195cd44d0990704fc4cf0e63d4` | `9381d1191a74451fbd44c658ad68bf90` | `notification_settings` | `NotificationSettingsContent.kt` | Estado local existente | Implementada |
| Sumar Emprendimiento | `acbc10636f734cd8a74b88e83bf69a6f` | `28bbf81a361848499a26e453f5f5c477` | `sumar_emprendimiento` | `EmprendimientoContent.kt` | `EmprendimientoViewModel` | Implementada |
| Offline / Error | `d744bea4a85648dfb46174a2623e7895` | `2144ad26530d4032b0c2fdec4684984a` | `offline` | `ui/features/common/OfflineContent.kt` | Connectivity observer | Implementada |
| Ayuda y Soporte | `c09c7ed80cb94d8f83c05e39ea958beb` | `442e686766e34fd09a4c690728e18368` | `support` | `SupportContent.kt` | Contenido institucional | Implementada |
| Legal y Privacidad | `370d4b0118f448099e76c9d248046d7e` | `4e3c7ad5f0a8426d8f194d80dbc93ea1` | `legal` | `LegalContent.kt` | Strings/enlaces | Implementada |
| Información Institucional | `e00067b8592b476a892730c6a5daa176` | `4ff8101db77a4910a678d617608e40f4` | `institutional_info` | `InstitutionalInfoContent.kt` | Contenido institucional | Implementada |
| Novedades | `0a8dedb5d4244e29bac46432065eb7ba` | `70a798051b70421fbdd979983b12717a` | `news_list` | `NewsListContent.kt` | Datos disponibles | Implementada |
| Detalle Noticia | `268db919d2ca46a2a025f0ec78f34ae2` | `3007465e82fb41ed8605c9c94d2a5919` | `news_detail/{newsId}` | `NewsDetailContent.kt` | Identificador de noticia | Implementada |
| Comercios Directorio | `8b1077e2bcd144be86bb3409998d9349` | `4c611673bdc7473faa6851e76991508d` | `bodegas_tab` / categorías | `CategoryListContent.kt` | Comercios Firestore | Implementada |
| Perfil Comercio | `069f20f25dbb4fbb95eb56488a9b273d` | `135805e4ef6f44c3b071f96163416d02` | `commerce_profile/{commerceId}` | `CommerceProfileContent.kt` | `CommerceProfileViewModel` | Implementada |
| Reseñas Usuarios | `22734e313bce408396c95e25c399ed35` | `695e54e6858749669bfeb792c3ea3ea8` | `commerce_reviews/{commerceId}` | `UserReviewsContent.kt` | Reseñas reales | Implementada |
| Calificar Comercio | `35b31714b0e14d3c95fc68305e1039f5` | `031b7d629ad546d5ad7dbd38732ba19e` | `rate_commerce/{commerceId}` | `RateCommerceContent.kt` | Repositorio de comercios | Implementada |
| Servicios Sello | `456bc31ed29f411ca4f541ad6a38300b` | `60dacfa4aa6346abaf7f9be02adab4d5` | `servicios_sello` | `ServiciosSelloContent.kt` | Contenido institucional | Implementada |
| Formulario Público | `89f5d7a0817e428f940122c249ebb1f6` | `94f0cbfe83e447d4872141e3de9d7c79` | `formulario/{formId}` | `PublicFormContent.kt` | `PublicFormViewModel` / esquema dinámico | Implementada |
| Pantalla de Éxito | `7abe3f76e2f5436abe9353090f3513ce` | `7c99358dda804ec88f93bef8aee35688` | `success` | `SuccessContent.kt` | Parámetros existentes | Implementada |
| Acceso Administración | `5d8f766e88344dfb8064d8767ad4c0a6` | `ef7350a58d8542d9a5d09458aef7be8c` | `admin_login` | `AdminLoginScreen.kt` | Auth/admin | Implementada |
| Panel Administrativo | `02d4d9ca126d4442bd1c1f60ca472950` | `3f44d9ec23294cde984718dedec4bc1e` | `admin_home` | `AdminHomeScreen.kt` | Repositorios admin | Implementada |
| Administrar Comercios | `f06bc3b56b0c4b3695d5169353422fab` | `233364263dd64305a9ccaa762706532e` | `admin_comercios` | `AdminComerciosScreen.kt` | `AdminComerciosViewModel` | Implementada |
| Administrar Zonas | `8618d5abeed545e9b18b29589030deed` | `c27e9556cf18498ba6b29d8f11b4790f` | `admin_zonas` | `AdminZonasScreen.kt` | `AdminZonasViewModel` | Implementada |
| Administrar Beneficios | `90bb8204527247df8027ee9e203fb8f1` | `9d63c6b657f345989f4d9eaca01c65d5` | `admin_beneficios` | `AdminBeneficiosScreen.kt` | `AdminBeneficiosViewModel` | Implementada |
| Administrar Usuarios | `0ef09b6d912c4e1eb53fb6e4d95e5091` | `c918274aa27c455095830a3a94b76e33` | `admin_usuarios` | `AdminUsuariosScreen.kt` | `AdminUsuariosViewModel` | Implementada |
| Administrar Formularios | `0a10db8dab354917a11bfeb7a61f90b0` | `89840d34c077423da552b23b94d63a26` | `admin_formularios` | `AdminFormulariosScreen.kt` | `AdminFormulariosViewModel` | Implementada |
| Administrar Notificaciones | `0676e948e4554d46b7ed3d0f2ed16dd8` | `58a3e221b8214fa8ab824878f613f5d0` | `admin_notificaciones` | `AdminNotificacionesScreen.kt` | `AdminNotificacionesViewModel` | Implementada |
| Administrar Administradores | `477472170dee4b1aa6ec01e5108cf352` | `100a38525963401aa9d83562254287ba` | `admin_administradores` | `AdminAdministradoresScreen.kt` | `AdminAdministradoresViewModel` | Implementada |

No existe una ruta Android separada para `Guía Rápida`; la ayuda contextual se implementa como coachmarks dentro de `MainScaffold`. `Galería`, filtros y bottom sheet del mapa son componentes/estados, no destinos independientes.
