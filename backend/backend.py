from flask import Flask, request, jsonify
import os
import openai
import requests
import json

app = Flask(__name__)

recetas = []

receta_mock = {
	"titulo": "Título de la receta creada",
	"descripcion": "Descripción de la receta creada",
	"duracion": "17 minutos",
	"dificultad": "baja/media/alta",
	"ingredientes": [
		"Ingrediente 01",
		"Ingrediente 02",
		"Ingrediente 03"
    ],
	"procedimiento": [
		{
			"titulo": "Paso 01",
			"instrucciones": [
				"instrucción 01",
				"instrucción 02",
				"instrucción 03"
			]
		},
		{
			"titulo": "Paso 02",
			"instrucciones": [
				"instrucción 01",
				"instrucción 02",
				"instrucción 03"
			]
		}
	]
}

AZURE_OPENAI_KEY = os.getenv("AZURE_OPENAI_KEY")  # Asegúrate de tener esta variable de entorno configurada
AZURE_DOMAIN = "sosltixlicenses"
AZURE_DEPLOYMENT = "gpt-4o"
AZURE_VERSION = "2024-02-01"
AZURE_ENDPOINT = f"https://{AZURE_DOMAIN}.openai.azure.com/openai/deployments/{AZURE_DEPLOYMENT}/chat/completions?api-version={AZURE_VERSION}"

def consultar_azure_openai(prompt):
    headers = {
        "Content-Type": "application/json",
        "api-key": AZURE_OPENAI_KEY
    }    
    payload = {
        "messages": [
            {"role": "system", "content": "You are an AI assistant that helps people find information."},
            {"role": "user", "content": prompt}
        ]
    }
    response = requests.post(AZURE_ENDPOINT, headers=headers, json=payload)
    if response.status_code == 200:
        response_data = response.json()
        respuesta_generada = response_data['choices'][0]['message']['content']
        return respuesta_generada
    else:
        raise Exception(f"Error en la solicitud a Azure OpenAI: {response.status_code}, {response.text}")

def generate_prompt(ingredientes, equipamiento, perfil, comensales, dificultad, duracion, intolerancias):
    # perfil de cocinero
    perfiles = {
        "basico": "Mi perfil como cocinero es el siguiente: tengo poca experiencia. Sé cortar y pelar ingredientes comunes. Normalmente hago recetas hirviendo o pasando por la plancha los ingredientes. El equipo con el que suelo trabajar son ollas, sartenes y cuchillos.",
        "intermedio": "Mi perfil como cocinero es el siguiente: cocino habitualmente todos los días. Controlo los puntos de cocción de carnes y pescados. Uso equipamiento de cocina más allá de ollas, cuchillos y sartenes, como por ejemplo, batidoras, pasapures, horno y cocción al vapor.",
        "avanzado": "Mi perfil como cocinero es el siguiente: la cocina es una pasión para mí. Me gusta utilizar técnicas avanzadas de cocina e innovar tanto en presentación como en fusión de ingredientes."
    }
    perfil_elegido = perfiles[perfil]

    # equipamiento disponible
    equipamiento = ', '.join(equipamiento)
    equipamiento = "Tengo los siguientes utensilios de cocina disponibles: " + equipamiento + "."

    # ingredientes disponibles
    ingredientes = ', '.join(ingredientes)
    ingredientes = "Tengo los siguientes ingredientes en la nevera: " + ingredientes + "."

    # propiedades receta: dificultad, comensales, duracion
    dificultades  = {
        "facil": "fácil",
        "media": "de dificultad media",
        "dificil": "que me suponga un reto"
    }
    dificultad_elegida = dificultades[dificultad]

    propiedades_receta = f"Estoy buscando una receta {dificultad_elegida}, para {comensales} personas que se pueda hacer en menos de {duracion} minutos."

    if intolerancias:
        intolerancias = ', '.join(intolerancias)
        propiedades_receta += f" Ten en cuenta que soy intolerante a: {intolerancias}."

    formato = 'Necesito que el formato de la respuesta venga en un json con el siguiente formato (el campo instrucciones puede estar dividido internamente en varios grupos por simplicidad si es necesario: {"titulo": "", "descripcion": "", "ingredientes": [], "procedimiento": [{"paso": "", "instrucciones": []},...]}. Solo devuelveme el json sin comentarios añadidos.'

    prompt = perfil_elegido + ' ' + equipamiento + ' ' + ingredientes + ' ' + propiedades_receta + ' ' + formato
    
    return prompt
    

@app.route('/generar', methods=['POST'])
def generar_receta():
    datos_receta = request.get_json()
    datos_necesarios = ["ingredientes", "equipamiento", "perfil", "comensales", "dificultad", "duracion", "intolerancias"]

    for dato in datos_necesarios: 
        if dato not in datos_receta:
            mensaje = {'error': 'Faltan datos requeridos'}
            return jsonify(mensaje), 400
        
    prompt = generate_prompt(datos_receta["ingredientes"], datos_receta["equipamiento"], datos_receta["perfil"], datos_receta["comensales"], datos_receta["dificultad"], datos_receta["duracion"], datos_receta["intolerancias"])
    
    # log para imprimir el prompt enviado
    print(prompt)

    receta = consultar_azure_openai(prompt)

    # convertimos a json
    try:
        receta = json.loads(receta)
    except json.JSONDecodeError as e:
        receta = {"error decode json": "No se pudo generar la receta, esperabamos un json pero la puta IA no nos dio un json."}
    
    # recordamos campos que vienen de la app
    receta["dificultad"] = datos_receta["dificultad"]
    receta["duracion"] = datos_receta["duracion"]

    # loggeamos la receta generada
    print(receta)
    print(type(receta))
    
    # Compruebo si ya hay una receta con el mismo título
    try:
        if recetas[receta['titulo']]:
            mensaje = {'error': 'La receta ya existe'}
            return mensaje, 400
    except KeyError:
        # for r in recetas:
        #     if r['titulo'] == receta['titulo']:
        #         mensaje = {'error': 'La receta ya existe'}
        #         return mensaje, 400
        
        # Si no existe, la añado a la lista de recetas (por defecto como no favorita)
        receta["favorita"] = False
        recetas.append(receta)
        return receta, 200

@app.route('/recetas/favoritas/agregar', methods=['POST'])
def agregar_favorita():
    data = request.get_json()
    titulo = data.get('titulo')
    if not titulo:
        return jsonify({'error': 'Falta el título de la receta en el cuerpo de la solicitud.'}), 400

    for receta in recetas:
        if receta["titulo"] == titulo:
            receta["favorita"] = True
            return f"{titulo} agregada a favoritos.", 200

    return f"No se encontró la receta: {titulo}.", 404

@app.route('/recetas/favoritas/borrar', methods=['POST'])
def borrar_favorita():
    data = request.get_json()
    titulo = data.get('titulo')
    if not titulo:
        return jsonify({'error': 'Falta el título de la receta en el cuerpo de la solicitud.'}), 400

    for receta in recetas:
        if receta["titulo"] == titulo:
            receta["favorita"] = False
            return f"{titulo} ha sido eliminada de favoritos.", 200

    return f"No se encontró la receta {titulo}.", 404

@app.route('/recetas', methods=['GET'])
def obtener_recetas():
    return recetas, 200

@app.route('/recetas/favoritas', methods=['GET'])
def obtener_recetas_favoritas():
    recetas_favoritas = []
    for receta in recetas:
        if receta["favorita"]:
            recetas_favoritas.append(receta)
    return recetas_favoritas, 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=True)
