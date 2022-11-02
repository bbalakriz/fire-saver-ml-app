FROM registry.access.redhat.com/ubi8/ubi-minimal AS compiler

COPY --chown=185 app.py fso_model.pkl requirements.txt /app/
RUN microdnf install -y python39 && python3 -m pip install --no-cache-dir -r /app/requirements.txt

WORKDIR /app/
EXPOSE 5000
USER 185

ENTRYPOINT ["python3", "app.py"]