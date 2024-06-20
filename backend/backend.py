from flask import Flask, request, jsonify
import os
from openai import AzureOpenAI

app = Flask(__name__)

recetas = []

AZURE_OPENAI_KEY = os.getenv("AZURE_OPENAI_KEY")  # Asegúrate de tener esta variable de entorno configurada
AZURE_DOMAIN = "sosltixlicenses"
AZURE_DEPLOYMENT = "gpt-4o"
AZURE_VERSION = "2024-02-01"
AZURE_ENDPOINT = f"https://{AZURE_DOMAIN}.openai.azure.com/openai/deployments/{AZURE_DEPLOYMENT}/chat/completions?api-version={AZURE_VERSION}"


def consultar_azure_openai(prompt):
    client = AzureOpenAI(
        api_key=AZURE_OPENAI_KEY,
        azure_endpoint=AZURE_ENDPOINT,
        api_version=AZURE_VERSION
    )
    response = client.chat.completions.create(
        model=AZURE_DEPLOYMENT,
        messages=[
            {"role": "system", "content": "Eres un asistente virtual que proporciona recetas de cocina."},
            {"role": "user", "content": prompt},
        ]
    )
    return response.choices[0].message.content


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
    response = app.response_class(
        response=receta.replace('```json\n', '').replace('```', ''),
        status=200,
        mimetype='application/json'
    )
    return response

@app.route('/recetas', methods=['GET'])
def obtener_recetas():
    return jsonify(recetas)

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=True)
