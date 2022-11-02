# 1. The predict function in this file takes-in the image url as input and invokes the predict function
# 2. This function is not used in the actual implementation as it will use the base64 encoded

import os
from flask import Flask, request, make_response
from werkzeug.utils import secure_filename
from fastai.vision.all import *

UPLOAD_FOLDER = '/tmp'
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg', 'gif'}

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
learner = load_learner('fso_model.pkl')


def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


@app.route('/health')
def ping():
    return {'success': 'pong'}, 200


@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return {'error': 'no image found.'}, 400

    file = request.files['image']
    if file.filename == '':
        return {'error': 'no image found.'}, 400

    if file and allowed_file(file.filename):
        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], "test.jpg")
        file.save(filepath)
        prediction = learner.predict(filepath)
        os.remove(filepath)
        return {'success': prediction[0]}, 200

    return {'error': 'something went wrong.'}, 500


if __name__ == '__main__':
    # Uncomment this line for running the app in DEV mode
    #    app.run(debug=False, host='0.0.0.0', port=5000)

    # PROD grade WSGI server
    from waitress import serve
    serve(app, host='0.0.0.0', port=5000)
