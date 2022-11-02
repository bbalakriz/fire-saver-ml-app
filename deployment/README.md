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

1. Go to [the RHOSAK Web UI](https://console.redhat.com/application-services/streams/kafkas) and create a topic with the name `images`
2. Create a new Service Account for the Kafka instance
3. Add the permissions for the Kafka instance to allow the service account for both consumption and production of messages
![image](https://user-images.githubusercontent.com/37283315/199456832-90b1b9a9-db42-45c7-8e59-5f4807fb65e6.png)
