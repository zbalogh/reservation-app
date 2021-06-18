
# Desk Reservation App

Desk reservation application based on Angular, Java, .NET Core technologies as well as Docker and Kubernetes. 


## The application can be installed via helm:


1. Add the repository to the helm repo sources

```
helm repo add reservation-app https://zbalogh.github.io/reservation-app/helm-charts
```


2. Install GKE Ingress based package with default values

```
helm install reservation reservation-app/reservation-app-gke-ingress
```


3. Install GKE Istio based package with default values

```
helm install reservation reservation-app/reservation-app-gke-istio
```


4. Install NGINX Ingress based package with default values

```
helm install reservation reservation-app/reservation-app-nginx-ingress
```


For more details, go to the **docs** folder: [Click Here](docs/readme.txt)
