###1. Deploy MinIO

```
oc login 
oc new-project ml-fire-saver
oc apply -f deployment/minio_all.yaml
```

Get the route for the minio Web UI from `oc get route.route.openshift.io/minio-web` and create three buckets with the names - normal, fire, smoke 

###2.Deploy Classifier ML app

```
oc apply -f deployment/classifier-app.yaml 
```
