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
    receta = consultar_azure_openai(prompt)
    return jsonify(receta), 201

@app.route('/recetas', methods=['GET'])
def obtener_recetas():
    return jsonify(recetas)

if __name__ == '__main__':
    app.run(debug=True)
