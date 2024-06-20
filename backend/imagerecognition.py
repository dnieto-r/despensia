import base64
from flask import Flask, request, jsonify
import os
from openai import AzureOpenAI

app = Flask(__name__)

AZURE_OPENAI_KEY = os.getenv("AZURE_OPENAI_KEY")  # Asegúrate de tener esta variable de entorno configurada
AZURE_DOMAIN = "sosltixlicenses"
AZURE_DEPLOYMENT = "gpt-4o"
AZURE_VERSION = "2024-02-01"
AZURE_ENDPOINT = f"https://{AZURE_DOMAIN}.openai.azure.com/openai/deployments/{AZURE_DEPLOYMENT}/chat/completions?api-version={AZURE_VERSION}"


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


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000, debug=True)
