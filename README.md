
# Desk Reservation App

Desk reservation application is based on Angular, Java, .NET Core technologies as well as Docker and Kubernetes. 


## The application can be installed via helm on Kubernetes


1. Add the repository to the helm repo sources

```
helm repo add reservation-app https://zbalogh.github.io/reservation-app
```


2. Install GKE (Google built-in Ingress Controller) based package with default values

```
helm install reservation reservation-app/reservation-app --set ingressControllerType=gke
```


3. Install AKS (Azure built-in Ingress Controller) based package with default values

```
helm install reservation reservation-app/reservation-app --set ingressControllerType=aks
```


4. Install ISTIO (Istio Gateway) based package with default values

```
helm install reservation reservation-app/reservation-app --set ingressControllerType=istio
```


5. Install NGINX (Ingress Controller) based package with default values

```
helm install reservation reservation-app/reservation-app --set ingressControllerType=nginx

OR

helm install reservation reservation-app/reservation-app
```


6. Install without creating Ingress (choose it if you want to manually configure your Ingress)

```
helm install reservation reservation-app/reservation-app --set ingressControllerType=no-ingress
```


## The application can be installed via docker-compose or docker stack


1. Run with the docker-compose

```
docker-compose -f docker-compose.yml -p reservation-app up -d
```


2. un with the docker stack

```
docker stack deploy -c docker-stack.yml reservation-app
```


For more details, go to the **docs** folder: [Click Here](docs/readme.txt)
