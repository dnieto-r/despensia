from flask import Flask, request, jsonify
import os
import openai
import requests
import json

app = Flask(__name__)

recetas = []

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
        
    assert datos_receta["perfil"] in ["basico", "intermedio", "avanzado"], "El perfil del cocinero debe ser 'basico', 'intermedio' o 'avanzado'"
    assert datos_receta["dificultad"] in ["facil", "media", "dificil"], "La dificultad de la receta debe ser 'facil', 'media' o 'dificil'"

    prompt = generate_prompt(datos_receta["ingredientes"], datos_receta["equipamiento"], datos_receta["perfil"], datos_receta["comensales"], datos_receta["dificultad"], datos_receta["duracion"], datos_receta["intolerancias"])
    
    # log para imprimir el prompt enviado
    print(prompt)

    receta = consultar_azure_openai(prompt)
    
    # loggeamos la receta generada
    print(f'LOGGER: RESPUESTA IA:\n\n {receta}')
    print(f'LOGGER: TIPO DE RESPUESTA IA:\n\n {type(receta)}')

    # convertimos a json
    try:
        receta = json.loads(receta)
    except json.JSONDecodeError as e:
        receta = {"error decode json": "No se pudo generar la receta, esperabamos un json pero la puta IA no nos dio un json."}
    
    # recordamos campos que vienen de la app
    receta["dificultad"] = datos_receta["dificultad"]
    receta["duracion"] = datos_receta["duracion"]

    
    # receta = {
    #     "dificultad": datos_receta["dificultad"],
    #     "duracion": datos_receta["duracion"],
    #     "titulo": "Salmón con salsa de pera, champiñones salteados y espinacas crujientes",
    #     "descripcion": "Una combinación innovadora que resalta los sabores del salmón con una salsa dulce de pera, acompañado de champiñones salteados y espinacas crujientes.",
    #     "ingredientes": [
    #         "Salmón fresco (2 filetes)",
    #         "Peras (2 unidades)",
    #         "Champiñones (200 g)",
    #         "Espinacas frescas (150 g)",
    #         "Aceite de oliva",
    #         "Mantequilla",
    #         "Ajo (2 dientes)",
    #         "Caldo de verduras",
    #         "Sal y pimienta al gusto"
    #     ],
    #     "procedimiento": [
    #         {
    #             "paso": "Preparación de la salsa de pera",
    #             "instrucciones": [
    #                 "Pelar y cortar las peras en trozos. En una olla, derretir una cucharada de mantequilla y añadir los trozos de pera con un poco de sal.",
    #                 "Cocinar a fuego medio-bajo hasta que las peras estén suaves, unos 10-15 minutos.",
    #                 "Transferir las peras cocidas a una licuadora y triturar hasta obtener una salsa suave. Si es necesario, añadir un poco de caldo de verduras para ajustar la consistencia. Reservar."
    #             ]
    #         },
    #         {
    #             "paso": "Preparación del salmón",
    #             "instrucciones": [
    #                 "Preparar los filetes de salmón secándolos con papel de cocina.",
    #                 "Sazonar con sal y pimienta.",
    #                 "En una sartén, calentar un poco de aceite de oliva a fuego medio-alto.",
    #                 "Cocinar el salmón con la piel hacia abajo primero, durante 3-4 minutos. Luego, voltear y cocinar por otros 3-4 minutos hasta que esté cocido pero aún jugoso por dentro."
    #             ]
    #         },
    #         {
    #             "paso": "Preparación de los champiñones salteados",
    #             "instrucciones": [
    #                 "Lavar y cortar los champiñones en láminas.",
    #                 "En una sartén, calentar un poco de aceite de oliva y una cucharada de mantequilla.",
    #                 "Añadir los champiñones y cocinar a fuego medio-alto hasta que estén dorados y tiernos, unos 5-7 minutos.",
    #                 "Sazonar con sal y pimienta al gusto. Reservar."
    #             ]
    #         },
    #         {
    #             "paso": "Preparación de las espinacas crujientes",
    #             "instrucciones": [
    #                 "Lavar y secar las espinacas.",
    #                 "En una bandeja para horno, distribuir las espinacas en una capa uniforme.",
    #                 "Rociar con un poco de aceite de oliva y sazonar con sal.",
    #                 "Hornear a 180°C durante unos 5-7 minutos o hasta que las espinacas estén crujientes.",
    #                 "Retirar del horno y reservar."
    #             ]
    #         },
    #         {
    #             "paso": "Armado del plato",
    #             "instrucciones": [
    #                 "Colocar una cama de espinacas crujientes en el centro de cada plato.",
    #                 "Colocar encima el filete de salmón cocido.",
    #                 "Añadir los champiñones salteados alrededor del salmón.",
    #                 "Salsear con la salsa de pera preparada.",
    #                 "Decorar con algunas hojas frescas de espinacas o perejil si se desea.",
    #                 "¡Servir caliente y disfrutar!"
    #             ]
    #         }
    #     ]
    # }

    return receta, 200

@app.route('/recetas', methods=['GET'])
def obtener_recetas():
    return jsonify(recetas)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=True)
