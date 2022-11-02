import base64
import os
from flask import Flask, request, make_response
from fastai.vision.all import *

FILEPATH = "/tmp/image.jpg"

app = Flask(__name__)
learner = load_learner('fso_model.pkl')


@app.route('/health')
def ping():
    return {'success': 'pong'}, 200


@app.route('/predict', methods=['POST'])
def predict():
    imgdata = base64.b64decode(request.data)
    with open(FILEPATH, 'wb') as f:
        f.write(imgdata)
    prediction = learner.predict(FILEPATH)
    print(prediction[0])
    return {'success': prediction[0]}, 200


if __name__ == '__main__':
    # Uncomment this line for running the app in DEV mode
    #    app.run(debug=False, host='0.0.0.0', port=5000)

    # PROD grade WSGI server
    from waitress import serve
    serve(app, host='0.0.0.0', port=5000)
