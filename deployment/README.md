### 1. Deploy MinIO

```
oc login 
oc new-project ml-fire-saver
oc apply -f deployment/minio_all.yaml
```

Get the route for the minio Web UI from `oc get route.route.openshift.io/minio-web` and create three buckets with the names - normal, fire, smoke 

### 2.Deploy Classifier ML app

```
oc apply -f deployment/classifier-app.yaml 
```

### 3. Create a Kafka instance in RHOSAK

Go to [the RHOSAK Web UI](https://console.redhat.com/application-services/streams/kafkas) and create a topic with the name `images`
