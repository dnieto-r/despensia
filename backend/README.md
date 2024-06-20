# Instrucciones para el Servidor Backend

Hay dos servidores
## backend.py: 
Servidor que atiende peticiones en su endpoint de /recetas.

Debe recibir la siguiente información:

POST al http://127.0.0.1:8000/recetas con el body:
```
{
  "ingredientes": ["salmon", "pimienta", "sal"],
  "equipamiento": ["olla grande", "colador"],
  "tipo": "cena",
  "dificultad": "Media",
  "duracion": "30 minutos",
  "intolerancias": ["Lactosa"]
}
```

Y se recibe una respuesta:
```
{
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
```

## imagerecognition.py: 
Servidor que atiende peticiones en su endpoint /imagen

POST /imagen en formato multipart. La imagen debe ir con la clave image

curl de ejemplo:

curl --request POST 'http://localhost:9000/imagen' --form 'image=@"/home/rps/Imágenes/ingredientes.jpg"'

Respuesta:
```
{
  "ingredientes": [
    "espárragos",
    "aguacate",
    "cebolleta",
    "tomate",
    "huevo",
    "jamón",
    "pimienta",
    "cáscara de huevo"
  ]
}
```

## Instalación de Requisitos

Antes de comenzar, asegúrate de tener Python 3 y pip instalados en tu sistema. Luego, sigue estos pasos:

1. Clona este repositorio en tu máquina local:

   ```bash
   git clone git@github.com:dnieto-r/despensia.git
   cd despensia/backend/

2. Instala los requerimientos del servidor:

    ```bash
    pip install -r requirements.txt

3. Levantar el servidor:

    ```bash
    python3 backend.py
