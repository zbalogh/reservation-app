
------------------------------
README file - Reservation App
------------------------------

#
# Create docker image, and push to docker hub
#

docker build -t zbalogh/reservation-api-server:latest -f Dockerfile .

docker build -t zbalogh/reservation-angular-ui:latest -f Dockerfile .



docker push zbalogh/reservation-api-server:latest

docker push zbalogh/reservation-angular-ui:latest


########################################################################################
########################################################################################
########################################################################################

#
# Deploy application into kubernetes
#

Apply all yaml files provided in the given folder:
kubectl apply -f <your-folder-name>


kubectl get services -o wide

kubectl get pods -o wide

kubectl get deployments -o wide

kubectl get nodes -o wide

kubectl get all -o wide


kubectl scale deployment <deployment-name> --replicas=0

kubectl scale deployment <deployment-name> --replicas=3



Delete all configurations and deployments provided in the given folder:
kubectl delete -f <your-folder-name>


########################################################################################
########################################################################################
########################################################################################

#
# Setup Istio
#

https://istio.io/latest/docs/setup/getting-started/#download

https://istio.io/latest/docs/setup/install/istioctl/

kubectl get namespaces --show-labels

kubectl label namespace default istio-injection=enabled


########################################################################################
########################################################################################
########################################################################################

#
# Installing with Helm
#

Helm example: https://github.com/technosophos/tscharts


How to Create Helm package:

cd helm/src

helm create reservation-app-gke-ingress

helm package reservation-app-gke-ingress

# copy tar.gz file into docs/helm-charts directory

cd ../..

helm repo index docs/helm-charts --url https://zbalogh.github.io/reservation-app/helm-charts



Install application from Helm package:

https://artifacthub.io/packages/helm/reservation-app/reservation-app-gke-ingress

helm repo add reservation https://zbalogh.github.io/reservation-app/helm-charts

helm install reservation reservation/reservation-app-gke-ingress

