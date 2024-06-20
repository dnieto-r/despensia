from flask import Flask, request, jsonify
import os
import openai
import requests
import json

app = Flask(__name__)

recetas = []

# endpoint: https://sosltixlicenses.openai.azure.com/
# Location/Region: eastus2
# API Key: 48cf638f858f4118807d59f71c33b122
# deployment: gpt-4o

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


@app.route('/recetas', methods=['POST'])
def agregar_receta():
    datos_receta = request.get_json()
    datos_necesarios = ["ingredientes", "equipamiento", "tipo", "dificultad", "duracion", "intolerancias"]

    for dato in datos_necesarios: 
        if dato not in datos_receta:
            mensaje = {'error': 'Faltan datos requeridos'}
            return jsonify(mensaje), 400
    prompt = 'Mi perfil como cocinero es el siguiente: la cocina es una pasión para mí. Me gusta utilizar técnicas avanzadas de cocina e innovar tanto en presentación como en fusión de ingredientes. Tengo los siguientes ingredientes en la nevera: salmón, peras, champiñones y espinacas. Tengo los siguientes utensilios de cocina disponibles: sartenes, ollas, horno, batidora, olla a presion, microhondas, freidora de aire, licuadora, soplete, mandolina, termometro, balanza. Estoy buscando una receta para 2 personas que me suponga un reto y que se pueda hacer en unos 90 minutos. Necesito que el formato de la respuesta venga en un json con el siguiente formato (el campo instrucciones puede estar dividido internamente en varios grupos por simplicidad si es necesario:{"titulo": "","descripcion": "","ingredientes": [],"utensilios": [],"instrucciones": ["paso_1": {"titulo": "","instrucciones": []},...]}'
    #receta = consultar_azure_openai(prompt)
    receta = {
        "dificultad": "media",
        "duracion": "17 minutos",
        "titulo": "Salmón con salsa de pera, champiñones salteados y espinacas crujientes",
        "descripcion": "Una combinación innovadora que resalta los sabores del salmón con una salsa dulce de pera, acompañado de champiñones salteados y espinacas crujientes.",
        "ingredientes": [
            "Salmón fresco (2 filetes)",
            "Peras (2 unidades)",
            "Champiñones (200 g)",
            "Espinacas frescas (150 g)",
            "Aceite de oliva",
            "Mantequilla",
            "Ajo (2 dientes)",
            "Caldo de verduras",
            "Sal y pimienta al gusto"
        ],
        "procedimiento": [
            {
                "titulo": "Preparación de la salsa de pera",
                "instrucciones": [
                    "Pelar y cortar las peras en trozos. En una olla, derretir una cucharada de mantequilla y añadir los trozos de pera con un poco de sal.",
                    "Cocinar a fuego medio-bajo hasta que las peras estén suaves, unos 10-15 minutos.",
                    "Transferir las peras cocidas a una licuadora y triturar hasta obtener una salsa suave. Si es necesario, añadir un poco de caldo de verduras para ajustar la consistencia. Reservar."
                ]
            },
            {
                "titulo": "Preparación del salmón",
                "instrucciones": [
                    "Preparar los filetes de salmón secándolos con papel de cocina.",
                    "Sazonar con sal y pimienta.",
                    "En una sartén, calentar un poco de aceite de oliva a fuego medio-alto.",
                    "Cocinar el salmón con la piel hacia abajo primero, durante 3-4 minutos. Luego, voltear y cocinar por otros 3-4 minutos hasta que esté cocido pero aún jugoso por dentro."
                ]
            },
            {
                "titulo": "Preparación de los champiñones salteados",
                "instrucciones": [
                    "Lavar y cortar los champiñones en láminas.",
                    "En una sartén, calentar un poco de aceite de oliva y una cucharada de mantequilla.",
                    "Añadir los champiñones y cocinar a fuego medio-alto hasta que estén dorados y tiernos, unos 5-7 minutos.",
                    "Sazonar con sal y pimienta al gusto. Reservar."
                ]
            },
            {
                "titulo": "Preparación de las espinacas crujientes",
                "instrucciones": [
                    "Lavar y secar las espinacas.",
                    "En una bandeja para horno, distribuir las espinacas en una capa uniforme.",
                    "Rociar con un poco de aceite de oliva y sazonar con sal.",
                    "Hornear a 180°C durante unos 5-7 minutos o hasta que las espinacas estén crujientes.",
                    "Retirar del horno y reservar."
                ]
            },
            {
                "titulo": "Armado del plato",
                "instrucciones": [
                    "Colocar una cama de espinacas crujientes en el centro de cada plato.",
                    "Colocar encima el filete de salmón cocido.",
                    "Añadir los champiñones salteados alrededor del salmón.",
                    "Salsear con la salsa de pera preparada.",
                    "Decorar con algunas hojas frescas de espinacas o perejil si se desea.",
                    "¡Servir caliente y disfrutar!"
                ]
            }
        ]
    }

    return receta, 200

@app.route('/recetas', methods=['GET'])
def obtener_recetas():
    return jsonify(recetas)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=True)
