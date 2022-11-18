### 1. Deploy MinIO

```
oc login 
oc new-project ml-fire-saver
oc apply -f deployment/minio_all.yaml
```

Get the route for the minio Web UI from `oc get route.route.openshift.io/minio-web` and create three buckets with the names - normal, fire, smoke 

![image](https://user-images.githubusercontent.com/37283315/199457005-24da3fcc-9c99-4a25-8a4b-5fb31f1950e8.png)


### 2.Deploy Classifier ML app

```
oc apply -f deployment/classifier-app.yaml 
```

### 3. Create a Kafka instance in RHOSAK

1. Go to [the RHOSAK Web UI](https://console.redhat.com/application-services/streams/kafkas) and create a topic with the name `images`
2. Create a new Service Account for the Kafka instance and during the process, note down the CLIENT_ID and CLIENT_SECRET
3. Add the permissions for the Kafka instance to allow the service account for both consumption and production of messages as shown below. 
![image](https://user-images.githubusercontent.com/37283315/199456832-90b1b9a9-db42-45c7-8e59-5f4807fb65e6.png)

### 4. Deploy event-consumer-app

Edit `fai-event-consumer.yaml` and set the point the value for the environment variables

1. `KAFKA_BOOTSTRAP_SERVERS` to the Kafka instance created in step 3
2. `CLIENT_ID` and `CLIENT_SECRET` to the values from the service account created in step 3

```
oc apply -f deployment/fai-event-consumer.yaml 
```

### 5. Deploy event-consumer-app

Edit `fai-event-emitter.yaml` and set the point the value for the environment variables

1. `KAFKA_BOOTSTRAP_SERVERS` to the Kafka instance created in step 3
2. `CLIENT_ID` and `CLIENT_SECRET` to the values from the service account created in step 3

```
oc apply -f deployment/fai-event-emitter.yaml 
```

### 6. Start capturing the activity

1. Get the application URL from the route `fai-event-emitter` and append `/imagestreamer.html` to it to access the image capturing application. 
2. Click the button "Start Image Streaming".

![image](https://user-images.githubusercontent.com/37283315/199464034-b99e1195-ba29-47f2-8b77-76558c3d95ea.png)


### 7. Start monitoring the activity

1. Get the application URL from the route `fai-event-consumer` and append `/plots.html` to it to access the monitoring application. 
2. Monitor the situation to see if there's any fire/smoke activity

![image](https://user-images.githubusercontent.com/37283315/199463944-e1f314c0-14a3-41c0-beef-e6cb1daadbf7.png)

