
------------------------------
README file - Reservation App
------------------------------

#
# Delete the existing docker images from your local image repo
#

docker rmi --force $(docker images -q 'zbalogh/reservation-api-server:latest' | uniq)

docker rmi --force $(docker images -q 'zbalogh/reservation-angular-ui:latest' | uniq)

docker rmi --force $(docker images -q 'zbalogh/reservation-dotnet-auth-server:latest' | uniq)



#
# Create new docker images in your local image repo
#

docker build -t zbalogh/reservation-api-server:latest -f Dockerfile .

docker build -t zbalogh/reservation-angular-ui:latest -f Dockerfile .

docker build -t zbalogh/reservation-dotnet-auth-server:latest -f Dockerfile .



#
# Push docker images to the Docker Hub
#

docker push zbalogh/reservation-api-server:latest

docker push zbalogh/reservation-angular-ui:latest

docker push zbalogh/reservation-dotnet-auth-server:latest




########################################################################################
########################################################################################
########################################################################################

#
# Run with the docker-compose
#

docker-compose -f docker-compose.yml -p reservation-app up -d
 
docker-compose ps

docker-compose -p reservation-app down




########################################################################################
########################################################################################
########################################################################################

#
# Run with the docker stack on docker swarm
#

docker stack deploy -c docker-stack.yml reservation-app

docker stack services ls

docker stack services reservation-app

docker stack ps reservation-app

docker stack rm reservation-app




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

---------------------------
How to Create Helm package:
---------------------------

cd helm/src

helm create reservation-app

helm package reservation-app

mkdir -p ../tmp/reservation-demo/helm-charts

mv *.tgz ../tmp/reservation-demo/helm-charts

helm repo index ../tmp/reservation-demo/helm-charts --url https://zbalogh.github.io/reservation-app



--------------------------------------
Install application from Helm package:
--------------------------------------

https://artifacthub.io/packages/helm/reservation-app/reservation-app


# add the repository to the helm repo sources
helm repo add reservation-app https://zbalogh.github.io/reservation-app

# install with default replicas=1
helm install reservation reservation-app/reservation-app

# install with replicas=2 (override the default values by the "--set" keyword)
helm install reservation reservation-app/reservation-app --set webguiReplicas=2 --set apiServerReplicas=2

# install with replicas=2 and alldeskNumber=40 (override the default values by the "--set" keyword)
helm install reservation reservation-app/reservation-app --set webguiReplicas=2 --set apiServerReplicas=2 --set alldeskNumber=40

# install with replicas=2, alldeskNumber=40, and set the passwords (override the default values by the "--set" keyword)
helm install reservation reservation-app/reservation-app --set webguiReplicas=2 --set apiServerReplicas=2 --set authServerReplicas=2 --set databasePassword=dbadmin123 --set adminUserPassword=pwd123 --set alldeskNumber=40

#install with alldeskNumber=40, and set the passwords (override the default values by the "--set" keyword)
helm install reservation reservation-app/reservation-app --set databasePassword=dbadmin123 --set adminUserPassword=pwd123 --set alldeskNumber=40

# install with GKE Ingress Controller
helm install reservation reservation-app/reservation-app --set ingressControllerType=gke

# install with AKS Ingress Controller
helm install reservation reservation-app/reservation-app --set ingressControllerType=aks

# install with ISTIO Ingress Gateway
helm install reservation reservation-app/reservation-app --set ingressControllerType=istio

# install with NGINX Ingress Controller (Default)
helm install reservation reservation-app/reservation-app --set ingressControllerType=nginx

# install without Ingress Controller
helm install reservation reservation-app/reservation-app --set ingressControllerType=no-ingress
