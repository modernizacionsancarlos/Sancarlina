package com.sancarlina.app.data.templates

import com.sancarlina.app.data.models.FormField
import com.sancarlina.app.data.models.FormSchema
import com.sancarlina.app.data.models.FormTemplate

object BuiltinFormTemplates {

    val DISTRICT_OPTIONS = listOf(
        "Omitir (no sé el distrito)",
        "Pareditas",
        "Chilecito",
        "Tres Esquinas",
        "Eugenio Bustos",
        "Villa San Carlos",
        "Villa Chacón",
        "La Consulta"
    )

    val RELEVAMIENTO_COMERCIO = FormTemplate(
        id = "relevamiento_comercio",
        name = "Relevamiento de Comercio",
        description = "Relevamiento municipal de comercios: datos, contacto, dirección y ubicación GPS en el mapa.",
        icon = "store",
        category = "comercios",
        templateSource = "relevamiento_comercio",
        schema = FormSchema(
            id = "relevamiento_comercio",
            title = "Relevamiento de Comercio - Municipio de San Carlos",
            description = "Formulario de relevamiento comercial oficial para la app GondolApp.",
            isPublic = true,
            acceptsResponses = true,
            status = "active",
            formPurpose = "commerce_registration",
            templateSource = "relevamiento_comercio",
            templateCategory = "comercios",
            fields = listOf(
                FormField(
                    id = "sec_datos",
                    type = "section",
                    label = "1. Datos Comerciales Principales"
                ),
                FormField(
                    id = "field_nombre",
                    type = "text",
                    label = "Nombre del Comercio o Emprendimiento",
                    required = true,
                    tenantMapping = "name",
                    placeholder = "Ej. Almacén Don Pedro"
                ),
                FormField(
                    id = "field_rubro",
                    type = "selector",
                    label = "Rubro / Actividad",
                    required = true,
                    tenantMapping = "industry",
                    options = listOf("Almacén", "Gastronomía", "Indumentaria", "Servicios", "Artesanías", "Otro")
                ),
                FormField(
                    id = "field_descripcion",
                    type = "textarea",
                    label = "Descripción o Reseña Corta",
                    required = false,
                    tenantMapping = "description",
                    placeholder = "Contanos brevemente sobre los productos o servicios que ofrecés..."
                ),
                FormField(
                    id = "sec_ubicacion",
                    type = "section",
                    label = "2. Ubicación y Distrito"
                ),
                FormField(
                    id = "field_localidad",
                    type = "selector",
                    label = "Localidad / Distrito",
                    required = false,
                    tenantMapping = "area_id",
                    helpText = "Si no sabes el distrito, puedes omitir este paso.",
                    options = DISTRICT_OPTIONS
                ),
                FormField(
                    id = "field_direccion",
                    type = "text",
                    label = "Dirección / Calle y Número",
                    required = false,
                    tenantMapping = "address",
                    placeholder = "Ej. San Martín 450"
                ),
                FormField(
                    id = "field_gps",
                    type = "gps",
                    label = "Ubicación en el Mapa GPS",
                    required = false,
                    tenantMapping = "geo_coordinates",
                    helpText = "Toca en el mapa para ajustar la ubicación exacta del comercio."
                ),
                FormField(
                    id = "sec_contacto_fotos",
                    type = "section",
                    label = "3. Contacto y Fotografía"
                ),
                FormField(
                    id = "field_telefono",
                    type = "phone",
                    label = "Teléfono o WhatsApp",
                    required = false,
                    tenantMapping = "contact_phone",
                    placeholder = "2622 123456"
                ),
                FormField(
                    id = "field_email",
                    type = "email",
                    label = "Correo Electrónico de Contacto",
                    required = false,
                    tenantMapping = "contact_email",
                    placeholder = "contacto@comercio.com"
                ),
                FormField(
                    id = "field_foto_portada",
                    type = "image",
                    label = "Foto Principal o Fachada",
                    required = false,
                    tenantMapping = "cover_url",
                    maxImages = 1,
                    allowMultiple = false,
                    helpText = "Imagen de portada o frente del establecimiento (máx 5 MB)."
                ),
                FormField(
                    id = "field_galeria",
                    type = "image",
                    label = "Fotos Adicionales (Galería)",
                    required = false,
                    tenantMapping = "gallery",
                    maxImages = 3,
                    allowMultiple = true,
                    helpText = "Subí hasta 3 fotos adicionales de tu local o productos (máx 5 MB c/u)."
                )
            )
        )
    )

    val RELEVAMIENTO_TURISMO = FormTemplate(
        id = "relevamiento_turismo",
        name = "Relevamiento Turismo",
        description = "Registro de prestadores turísticos y comercios para la app oficial del Municipio.",
        icon = "travel_explore",
        category = "turismo",
        templateSource = "relevamiento_turismo",
        schema = FormSchema(
            id = "relevamiento_turismo",
            title = "Relevamiento Turístico - Municipio de San Carlos",
            description = "Registro oficial de actividades, alojamientos, bodegas y experiencias turísticas.",
            isPublic = true,
            acceptsResponses = true,
            status = "active",
            formPurpose = "commerce_registration",
            templateSource = "relevamiento_turismo",
            templateCategory = "turismo",
            fields = listOf(
                FormField(
                    id = "sec_datos_turismo",
                    type = "section",
                    label = "1. Prestador Turístico"
                ),
                FormField(
                    id = "field_nombre_turismo",
                    type = "text",
                    label = "Nombre del Establecimiento / Prestador",
                    required = true,
                    tenantMapping = "name",
                    placeholder = "Ej. Cabañas del Valle"
                ),
                FormField(
                    id = "field_rubro_turismo",
                    type = "selector",
                    label = "Categoría Turística",
                    required = true,
                    tenantMapping = "industry",
                    options = listOf("Alojamiento", "Bodega / Viñedo", "Restaurante / Finca", "Turismo Aventura", "Guía Turístico", "Otro")
                ),
                FormField(
                    id = "field_desc_turismo",
                    type = "textarea",
                    label = "Descripción del Servicio",
                    required = false,
                    tenantMapping = "description"
                ),
                FormField(
                    id = "sec_ubicacion_turismo",
                    type = "section",
                    label = "2. Ubicación en San Carlos"
                ),
                FormField(
                    id = "field_localidad_turismo",
                    type = "selector",
                    label = "Localidad / Distrito",
                    required = false,
                    tenantMapping = "area_id",
                    helpText = "Si no sabes el distrito, puedes omitir este paso.",
                    options = DISTRICT_OPTIONS
                ),
                FormField(
                    id = "field_gps_turismo",
                    type = "gps",
                    label = "Coordenadas GPS",
                    required = false,
                    tenantMapping = "geo_coordinates"
                ),
                FormField(
                    id = "sec_fotos_turismo",
                    type = "section",
                    label = "3. Galería de Fotos"
                ),
                FormField(
                    id = "field_foto_portada_turismo",
                    type = "image",
                    label = "Foto Principal",
                    required = false,
                    tenantMapping = "cover_url",
                    maxImages = 1
                ),
                FormField(
                    id = "field_galeria_turismo",
                    type = "image",
                    label = "Fotos de Galería",
                    required = false,
                    tenantMapping = "gallery",
                    maxImages = 3,
                    allowMultiple = true
                )
            )
        )
    )

    val ALL_TEMPLATES = listOf(RELEVAMIENTO_COMERCIO, RELEVAMIENTO_TURISMO)
}
