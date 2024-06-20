from flask import Flask, request, jsonify
import os
import openai

app = Flask(__name__)

recetas = []

ia = False

if ia:
    api_key = "48cf638f858f4118807d59f71c33b122"
    #api_key = os.getenv("OPENAI_API_KEY")
    openai.api_key = api_key
    model="gpt-3.5-turbo"
    prompt = "Capital de España"
    try:
        response = openai.Completion.create(
            model=model,
            prompt=prompt,
            max_tokens=50
        )
        respuesta_generada = response['choices'][0]['text'].strip()
        print("Respuesta del modelo:", respuesta_generada)
    except Exception as e:
        print("Error al llamar a OpenAI:", str(e))

@app.route('/recetas', methods=['POST'])
def agregar_receta():
    datos_receta = request.get_json()
    datos_necesarios = ["ingredientes", "equipamiento", "tipo", "dificultad", "duracion", "intolerancias"]

    for dato in datos_necesarios: 
        if dato not in datos_receta:
            mensaje = {'error': 'Faltan datos requeridos'}
            return jsonify(mensaje), 400
    receta = {
        "id": "001",
        "titulo": "Salmón al horno",
        "descripcion": "Receta de salmón al horno con verduras",
        "duración": "30 minutos",
        "dificultad": "Media",
        "ingredientes": ["salmón", "pimienta", "sal", "pimiento", "cebolla"],
        "pasos": ["Precalentar el horno a 180 grados", 
                  "Cortar las verduras", 
                  "Colocar el salmón en una bandeja",
                  "Añadir las verduras",
                  "Hornear durante 20 minutos"]
    }
    return jsonify(receta), 201  # Código 201 para indicar creación exitosa

@app.route('/recetas', methods=['GET'])
def obtener_recetas():
    return jsonify(recetas)

if __name__ == '__main__':
    app.run(debug=True)
