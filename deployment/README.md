**1. Deploy MinIO

```
oc login 
oc new-project ml-fire-saver
oc apply -f deployment/minio_all.yaml
```

Get the minio `web-ui` url from the `oc get route.route.openshift.io/minio-web` and create three buckets with the names - normal, fire, smoke 

**Deploy Classifier ML app
