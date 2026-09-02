from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    BaseDocTemplate,
    Frame,
    PageBreak,
    PageTemplate,
    Paragraph,
    Spacer,
    Table,
    TableStyle,
)

ROOT = Path(__file__).resolve().parents[2]
OUTPUT = ROOT / "output" / "pdf" / "guia_registradores_gondolapp_8.8.1.pdf"
OUTPUT.parent.mkdir(parents=True, exist_ok=True)

FONT_DIR = Path("C:/Windows/Fonts")
pdfmetrics.registerFont(TTFont("GondolRegular", str(FONT_DIR / "arial.ttf")))
pdfmetrics.registerFont(TTFont("GondolBold", str(FONT_DIR / "arialbd.ttf")))

OLIVE = colors.HexColor("#66743B")
OLIVE_DARK = colors.HexColor("#27320C")
OLIVE_LIGHT = colors.HexColor("#DDE8B9")
BURGUNDY = colors.HexColor("#9E334A")
CREAM = colors.HexColor("#F7F4EA")
INK = colors.HexColor("#1F241C")
MUTED = colors.HexColor("#5E6257")
GOLD = colors.HexColor("#A87810")
WHITE = colors.white

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name="GuideTitle",
    fontName="GondolBold",
    fontSize=25,
    leading=29,
    textColor=WHITE,
    alignment=TA_LEFT,
    spaceAfter=7,
))
styles.add(ParagraphStyle(
    name="GuideSubtitle",
    fontName="GondolRegular",
    fontSize=11.5,
    leading=16,
    textColor=colors.HexColor("#F4F7EA"),
))
styles.add(ParagraphStyle(
    name="Section",
    fontName="GondolBold",
    fontSize=14.5,
    leading=17,
    textColor=OLIVE_DARK,
    spaceBefore=4,
    spaceAfter=8,
))
styles.add(ParagraphStyle(
    name="BodyGuide",
    fontName="GondolRegular",
    fontSize=9.6,
    leading=13.2,
    textColor=INK,
    spaceAfter=5,
))
styles.add(ParagraphStyle(
    name="SmallGuide",
    fontName="GondolRegular",
    fontSize=8.5,
    leading=12,
    textColor=MUTED,
))
styles.add(ParagraphStyle(
    name="CardTitle",
    fontName="GondolBold",
    fontSize=11,
    leading=14,
    textColor=OLIVE_DARK,
    spaceAfter=3,
))
styles.add(ParagraphStyle(
    name="StepNumber",
    fontName="GondolBold",
    fontSize=16,
    leading=19,
    textColor=WHITE,
    alignment=TA_CENTER,
))
styles.add(ParagraphStyle(
    name="Status",
    fontName="GondolBold",
    fontSize=10,
    leading=13,
    alignment=TA_CENTER,
))


def header_card(title, subtitle):
    content = [[Paragraph(title, styles["GuideTitle"])], [Paragraph(subtitle, styles["GuideSubtitle"])]]
    table = Table(content, colWidths=[174 * mm])
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), OLIVE),
        ("BOX", (0, 0), (-1, -1), 0, OLIVE),
        ("LEFTPADDING", (0, 0), (-1, -1), 12 * mm),
        ("RIGHTPADDING", (0, 0), (-1, -1), 12 * mm),
        ("TOPPADDING", (0, 0), (-1, 0), 10 * mm),
        ("BOTTOMPADDING", (0, -1), (-1, -1), 10 * mm),
    ]))
    return table


def info_card(title, text, background=colors.white, accent=OLIVE):
    table = Table([[Paragraph(title, styles["CardTitle"]), Paragraph(text, styles["BodyGuide"])]], colWidths=[48 * mm, 118 * mm])
    table.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), background),
        ("LINEBEFORE", (0, 0), (0, -1), 4, accent),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 6 * mm),
        ("RIGHTPADDING", (0, 0), (-1, -1), 5 * mm),
        ("TOPPADDING", (0, 0), (-1, -1), 3 * mm),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3 * mm),
        ("BOX", (0, 0), (-1, -1), 0.5, colors.HexColor("#D6CEBC")),
    ]))
    return table


def step(number, title, text):
    number_box = Table([[Paragraph(str(number), styles["StepNumber"])]], colWidths=[12 * mm], rowHeights=[12 * mm])
    number_box.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), BURGUNDY),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
    ]))
    description = [Paragraph(title, styles["CardTitle"]), Paragraph(text, styles["BodyGuide"])]
    row = Table([[number_box, description]], colWidths=[16 * mm, 150 * mm])
    row.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 0),
        ("RIGHTPADDING", (0, 0), (-1, -1), 3 * mm),
        ("TOPPADDING", (0, 0), (-1, -1), 1 * mm),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 1 * mm),
    ]))
    return row


def status_table():
    rows = [
        ["PENDIENTE", "Guardado en el celular. Espera conexión.", GOLD],
        ["ENVIANDO", "La aplicación está sincronizando.", OLIVE],
        ["ENVIADO", "La respuesta llegó al sistema municipal.", colors.HexColor("#436B2A")],
        ["ERROR", "Quedó guardado. Revisar sesión o usar Reintentar.", BURGUNDY],
    ]
    cells = []
    for label, text, color in rows:
        cells.append([
            Paragraph(f'<font color="{color.hexval()}">{label}</font>', styles["Status"]),
            Paragraph(text, styles["BodyGuide"]),
        ])
    table = Table(cells, colWidths=[34 * mm, 132 * mm])
    commands = [
        ("GRID", (0, 0), (-1, -1), 0.5, colors.HexColor("#D6CEBC")),
        ("VALIGN", (0, 0), (-1, -1), "MIDDLE"),
        ("LEFTPADDING", (0, 0), (-1, -1), 4 * mm),
        ("RIGHTPADDING", (0, 0), (-1, -1), 4 * mm),
        ("TOPPADDING", (0, 0), (-1, -1), 2.2 * mm),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 2.2 * mm),
    ]
    for index in range(len(cells)):
        commands.append(("BACKGROUND", (0, index), (-1, index), CREAM if index % 2 == 0 else WHITE))
    table.setStyle(TableStyle(commands))
    return table


def draw_page(canvas, doc):
    canvas.saveState()
    canvas.setFillColor(CREAM)
    canvas.rect(0, 0, A4[0], A4[1], stroke=0, fill=1)
    canvas.setFillColor(OLIVE)
    canvas.rect(0, A4[1] - 9 * mm, A4[0], 9 * mm, stroke=0, fill=1)
    canvas.setFont("GondolRegular", 8)
    canvas.setFillColor(MUTED)
    canvas.drawString(18 * mm, 10 * mm, "GondolApp 8.8.1 - Guía interna de trabajo en calle")
    canvas.drawRightString(A4[0] - 18 * mm, 10 * mm, f"Página {doc.page}")
    canvas.restoreState()


doc = BaseDocTemplate(
    str(OUTPUT),
    pagesize=A4,
    leftMargin=18 * mm,
    rightMargin=18 * mm,
    topMargin=16 * mm,
    bottomMargin=18 * mm,
    title="Guía para registradores - GondolApp 8.8.1",
    author="Municipalidad de San Carlos - Modernización",
)
frame = Frame(doc.leftMargin, doc.bottomMargin, doc.width, doc.height, id="content")
doc.addPageTemplates([PageTemplate(id="guide", frames=[frame], onPage=draw_page)])

story = [
    header_card(
        "Guía rápida para registradores",
        "Cómo relevar comercios con GondolApp, incluso sin conexión a internet.",
    ),
    Spacer(1, 5 * mm),
    Paragraph("Objetivo del operativo", styles["Section"]),
    Paragraph(
        "Registrar comercios, locales, bodegas y emprendimientos desde el celular. Cada persona trabaja con un usuario individual y con los formularios que la administración le habilitó.",
        styles["BodyGuide"],
    ),
    Spacer(1, 3 * mm),
    info_card(
        "Antes de salir",
        "1. Instalar o actualizar GondolApp. 2. Iniciar sesión con el usuario entregado. 3. Abrir una vez los formularios con internet para que queden disponibles sin conexión. 4. Verificar batería, ubicación y cámara.",
        OLIVE_LIGHT,
        OLIVE,
    ),
    Spacer(1, 4 * mm),
    info_card(
        "Tu usuario",
        "Es personal. No compartir contraseña ni usar la cuenta de otro registrador. Si no aparece Registro en calle, solicitar al administrador que revise el rol y los formularios asignados.",
        colors.white,
        BURGUNDY,
    ),
    Spacer(1, 5 * mm),
    Paragraph("Recorrido en 30 segundos", styles["Section"]),
    step(1, "Ingresar", "Abrir GondolApp e iniciar sesión con el correo y contraseña asignados."),
    step(2, "Abrir Registro en calle", "Entrar en Perfil y tocar el botón rojo Registro en calle."),
    step(3, "Elegir formulario", "Seleccionar el formulario habilitado y tocar el botón fijo Iniciar formulario."),
    step(4, "Completar y revisar", "Cargar los datos del local, ubicación y adjuntos. Revisar la vista previa antes de continuar."),
    step(5, "Guardar y seguir", "Guardar y enviar. Luego usar Registrar otro local para comenzar inmediatamente un nuevo registro."),
    PageBreak(),
    header_card("Paso a paso en la calle", "Un flujo pensado para registrar varios locales seguidos sin volver al inicio."),
    Spacer(1, 5 * mm),
    step(1, "Seleccioná el formulario correcto", "La pantalla muestra solo los formularios publicados, abiertos a respuestas y asignados a tu usuario o rol."),
    step(2, "Completá los campos obligatorios", "Los campos con * son obligatorios. Revisá especialmente nombre comercial, dirección, distrito, contacto y ubicación GPS."),
    step(3, "Agregá fotos o archivos", "La aplicación copia los adjuntos al almacenamiento privado del celular. No borres la aplicación mientras existan envíos pendientes."),
    step(4, "Guardá la respuesta", "Con internet, se envía en el momento. Sin internet, queda guardada y se enviará automáticamente cuando vuelva la conexión."),
    step(5, "Revisá la vista previa", "La pantalla final muestra lo respondido con nombres legibles. Podés tocar Editar esta respuesta para corregirla."),
    step(6, "Continuá con el siguiente local", "Tocá Registrar otro local. El mismo formulario queda disponible para repetir el proceso de forma ágil."),
    Spacer(1, 4 * mm),
    Paragraph("Estados de sincronización", styles["Section"]),
    status_table(),
    Spacer(1, 5 * mm),
    info_card(
        "Sincronización manual",
        "Si una respuesta no salió automáticamente, abrir Registro en calle o Envíos de formularios y tocar Sincronizar ahora. No hace falta volver a completar el formulario.",
        OLIVE_LIGHT,
        OLIVE,
    ),
    PageBreak(),
    header_card("Correcciones y control", "Qué hacer después de guardar y cómo se completa el circuito administrativo."),
    Spacer(1, 7 * mm),
    Paragraph("Si detectás un error", styles["Section"]),
    step(1, "Abrí Registros recientes", "Buscá la respuesta y tocá el ícono Editar. También podés entrar desde Envíos de formularios."),
    step(2, "Corregí y guardá", "La respuesta conserva su identidad. Si ya había sido enviada, vuelve a Pendiente de revisión sin crear un duplicado."),
    step(3, "Confirmá el estado", "Esperá Enviado o usá Sincronizar ahora. Si aparece Error, verificá que la sesión corresponda al usuario original."),
    Spacer(1, 5 * mm),
    Paragraph("Qué prepara la administración", styles["Section"]),
    info_card("1. Crear usuario", "Generar una cuenta individual con rol registrador desde el panel Web.", colors.white, OLIVE),
    Spacer(1, 2 * mm),
    info_card("2. Asignar formularios", "Publicar el esquema, habilitar la recepción y asignarlo por rol o por usuario.", colors.white, OLIVE),
    Spacer(1, 2 * mm),
    info_card("3. Recibir en tiempo real", "Cuando un envío llega a Firestore, aparece en el panel administrativo. Los envíos offline aparecen al reconectarse.", colors.white, OLIVE),
    Spacer(1, 2 * mm),
    info_card("4. Revisar y aprobar", "Leer la respuesta con etiquetas claras, revisar adjuntos y aprobar, rechazar o publicar el comercio.", colors.white, OLIVE),
    Spacer(1, 4 * mm),
    Paragraph("Checklist del registrador", styles["Section"]),
    Paragraph("[ ] Usuario correcto  [ ] Formularios visibles  [ ] GPS habilitado  [ ] Cámara disponible  [ ] Pendientes sincronizados al finalizar", styles["BodyGuide"]),
    Spacer(1, 4 * mm),
    info_card(
        "Regla de oro",
        "Antes de terminar la jornada, conectarse a internet, tocar Sincronizar ahora y confirmar que todos los registros indiquen Enviado.",
        colors.HexColor("#F5D7DC"),
        BURGUNDY,
    ),
    Spacer(1, 5 * mm),
    Paragraph(
        "Fuente funcional: flujo implementado y compilado localmente en GondolApp Android 8.8.1 (versionCode 75). La creación de usuarios y asignación de permisos Web debe publicarse antes del operativo.",
        styles["SmallGuide"],
    ),
]

doc.build(story)
print(OUTPUT)
