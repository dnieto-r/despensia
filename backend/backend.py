from flask import Flask, request, jsonify
import os
import openai
import requests
import json
import argparse
import base64
from openai import AzureOpenAI

app = Flask(__name__)

recetas = []

mock_receta = False
mock_imagen = False

receta_mock = {'titulo': 'Salmón a la Plancha con Pimienta', 'descripcion': 'Sencilla y rápida receta de salmón a la plancha con pimienta, ideal para un almuerzo ligero y saludable.', 'ingredientes': ['3 filetes de salmón', 'Sal al gusto', 'Pimienta al gusto', 'Aceite de oliva (para la plancha)'], 'procedimiento': [{'paso': 1, 'instrucciones': ['Seca los filetes de salmón con toallas de papel.']}, {'paso': 2, 'instrucciones': ['Sazona ambos lados de los filetes con sal y pimienta al gusto.']}, {'paso': 3, 'instrucciones': ['Calienta una sartén grande a fuego medio-alto y añade un poco de aceite de oliva.']}, {'paso': 4, 'instrucciones': ['Cuando el aceite esté caliente, coloca los filetes de salmón en la sartén con la piel hacia abajo.']}, {'paso': 5, 'instrucciones': ['Cocina los filetes durante 4-5 minutos o hasta que la piel esté crujiente y dorada.']}, {'paso': 6, 'instrucciones': ['Voltea los filetes y cocina por otros 3-4 minutos, hasta que estén cocidos a tu gusto.']}, {'paso': 7, 'instrucciones': ['Sirve los filetes de salmón calientes acompañados de tu guarnición preferida.']}]}
imagen_mock = {'created': 1718902320, 'data': [{'content_filter_results': {'hate': {'filtered': False, 'severity': 'safe'}, 'self_harm': {'filtered': False, 'severity': 'safe'}, 'sexual': {'filtered': False, 'severity': 'safe'}, 'violence': {'filtered': False, 'severity': 'safe'}}, 'prompt_filter_results': {'hate': {'filtered': False, 'severity': 'safe'}, 'profanity': {'detected': False, 'filtered': False}, 'self_harm': {'filtered': False, 'severity': 'safe'}, 'sexual': {'filtered': False, 'severity': 'safe'}, 'violence': {'filtered': False, 'severity': 'safe'}}, 'revised_prompt': 'Create an appetizing image featuring a simple and quick grilled salmon recipe with pepper. The dish should appear perfect for a light and healthy lunch, with the grilled salmon emphasizing its char marks and the pepper flakes adding a touch of spice. All of this should be plated delicately to represent a nutritious meal.', 'url': 'https://dalleprodsec.blob.core.windows.net/private/images/0953f3bb-5953-49fe-9982-6930cecbc9a5/generated_00.png?se=2024-06-21T16%3A52%3A10Z&sig=7Zc5RgyDH%2BtfaxGjxxMPfZoZTePa4i%2F%2B%2Fctt0if6BQ4%3D&ske=2024-06-27T13%3A22%3A41Z&skoid=e52d5ed7-0657-4f62-bc12-7e5dbb260a96&sks=b&skt=2024-06-20T13%3A22%3A41Z&sktid=33e01921-4d64-4f8c-a055-5bdaffd5e33d&skv=2020-10-02&sp=r&spr=https&sr=b&sv=2020-10-02'}]}

AZURE_OPENAI_KEY = os.getenv("AZURE_OPENAI_KEY")  # Asegúrate de tener esta variable de entorno configurada
AZURE_OPENAI_KEY_IMAGES = os.getenv("AZURE_OPENAI_KEY_IMAGES")  # Asegúrate de tener esta variable de entorno configurada
AZURE_DOMAIN = "sosltixlicenses"
AZURE_DOMAIN_IMAGES = "cdolicensesgpt4-v-swedenc"
AZURE_DEPLOYMENT = "gpt-4o"
AZURE_DEPLOYMENT_IMAGES = "dall-e"
AZURE_VERSION = "2024-02-01"
AZURE_ENDPOINT = f"https://{AZURE_DOMAIN}.openai.azure.com/openai/deployments/{AZURE_DEPLOYMENT}/chat/completions?api-version={AZURE_VERSION}"
AZURE_ENDPOINT_IMAGES = f"https://{AZURE_DOMAIN_IMAGES}.openai.azure.com/openai/deployments/{AZURE_DEPLOYMENT_IMAGES}/images/generations?api-version={AZURE_VERSION}"

def image_azure_openai(base64_image):
    client = AzureOpenAI(
        api_key=AZURE_OPENAI_KEY,
        azure_endpoint=AZURE_ENDPOINT,
        api_version=AZURE_VERSION
    )
    response = client.chat.completions.create(
        model=AZURE_DEPLOYMENT,
        messages=[
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": '¿Qué ingredientes hay en ésta imagen? Necesito que el formato de la respuesta venga en un json con el siguiente formato : {"ingredientes": ["tomate", "arroz"]}'
                    },
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/jpeg;base64,{base64_image}"
                        }
                    }
                ]
            }
        ]
    )
    return response.choices[0].message.content

def consultar_azure_openai(prompt, endpoint, api_key, mode='text'):
    headers = {
        "Content-Type": "application/json",
        "api-key": api_key
    }    
    if mode == 'image':
        payload = {
            "prompt": prompt,
            "n": 1,  # Number of images to generate
            "size": "1792x1024"  # Size of the generated images
        }
    else:
        payload = {
            "messages": [
                {"role": "user", "content": prompt}
            ]
        }
    response = requests.post(endpoint, headers=headers, json=payload)
    if response.status_code == 200:
        response_data = response.json()
        if mode == 'image':
            respuesta_generada = response_data
        elif mode == 'text':
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
    
@app.route('/imagen', methods=['POST'])
def image_recognition():
    if request.files.get("image") is None:
        message = {'error': 'Missing image in multipart form data'}
        return jsonify(message), 400
    f = request.files["image"]
    base64_image = base64.b64encode(f.read()).decode('utf-8')
    message = image_azure_openai(base64_image)
    response = app.response_class(
        response= message.replace('```json\n', '').replace('```', ''),
        status=200,
        mimetype='application/json'
    )
    return response

@app.route('/generar', methods=['POST'])
def generar_receta():
    datos_receta = request.get_json()
    datos_necesarios = ["ingredientes", "equipamiento", "perfil", "comensales", "dificultad", "duracion", "intolerancias"]

    for dato in datos_necesarios: 
        if dato not in datos_receta:
            mensaje = {'error': 'Faltan datos requeridos'}
            return jsonify(mensaje), 400
        
    assert datos_receta["perfil"] in ["basico", "intermedio", "avanzado"], "El perfil del cocinero debe ser 'basico', 'intermedio' o 'avanzado'"
    assert datos_receta["dificultad"] in ["facil", "media", "dificil"], "La dificultad de la receta debe ser 'facil', 'media' o 'dificil'"

    prompt = generate_prompt(datos_receta["ingredientes"], datos_receta["equipamiento"], datos_receta["perfil"], datos_receta["comensales"], datos_receta["dificultad"], datos_receta["duracion"], datos_receta["intolerancias"])
    
    # log para imprimir el prompt enviado
    print(prompt)

    if mock_receta:
        receta = receta_mock
    else:
        receta = consultar_azure_openai(prompt, AZURE_ENDPOINT, AZURE_OPENAI_KEY, 'text')
        receta = receta.replace("```json\n", "").replace("```", "")
        # convertimos a json
        try:
            receta = json.loads(receta)
        except json.JSONDecodeError as e:
            receta = {"error decode json": "No se pudo generar la receta, esperabamos un json pero la puta IA no nos dio un json."}
        
    # Generamos la imagen del plato
    if mock_imagen:
        imagen = imagen_mock
    else:
        receta_descripcion = receta["descripcion"]
        prompt_imagen = f"Quiero una imagen de la receta {receta_descripcion} en un plato negro y con efecto realista y sin sombra."
        print(prompt_imagen)
        imagen = consultar_azure_openai(prompt_imagen, AZURE_ENDPOINT_IMAGES, AZURE_OPENAI_KEY_IMAGES, 'image')
    url_imagen = imagen['data'][0]['url']
    
    # loggeamos la receta generada
    print(f'LOGGER: RESPUESTA IA:\n\n {receta}')
    print(f'LOGGER: TIPO DE RESPUESTA IA:\n\n {type(receta)}')

    # recordamos campos que vienen de la app
    receta["dificultad"] = datos_receta["dificultad"]
    receta["duracion"] = datos_receta["duracion"]

    titulo_receta = receta["titulo"]
    print(titulo_receta)
    try:
        for r in recetas:
            if r["titulo"] == titulo_receta:
                return "Ya existe una receta con ese nombre", 400 
    except KeyError:
        pass
    receta["favorita"] = False
    receta["imagen"] = url_imagen
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
    # Crear el analizador de argumentos
    parser = argparse.ArgumentParser(description='Run the Flask app.')
    # Añadir el argumento opcional 'port' con valor por defecto 8000
    parser.add_argument('-p', '--port', type=int, default=8000, help='Port number to run the server on (default: 8000)')
    # Parsear los argumentos
    args = parser.parse_args()

    # Obtener el valor del puerto
    port = args.port
    # Iniciar la aplicación Flask en el puerto especificado
    app.run(host='0.0.0.0', port=port, debug=True)