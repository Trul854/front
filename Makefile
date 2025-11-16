#	Registro
stA:
	curl -X POST http://localhost:8080/api/register \
		-H "Content-Type: application/json" \
		-d '{"username":"wawa","password":"123456","role":"USER"}' | jq

#	Inicio de sesión
stB:
	TOKEN=$$(curl -s -X POST http://localhost:8080/api/login \
		-H "Content-Type: application/json" \
		-d '{"username":"wawa","password":"123456"}' | jq -r '.token'); \
	echo $$TOKEN > token.txt
	cat token.txt

#	Prueba
stC:
	TOKEN=$$(cat token.txt); \
	curl -s -X GET http://localhost:8080/api/hello \
		-H "Content-Type: application/json" \
		-H "Authorization: Bearer $$TOKEN"

all: stA \
	stB \
	stC