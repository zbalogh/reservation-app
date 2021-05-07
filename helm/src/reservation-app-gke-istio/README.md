
ReservationApp repository with Istio Ingress Gateway.


1. Before install this package, you need to setup Istio in your Kubernetes cluster.

https://istio.io/latest/docs/setup/install/


2. After Istio setup, you have to enable istio-injection in your namespace (e.g. default namespace):

kubectl label namespace default istio-injection=enabled


3. Install this helm chart in your Kubernetes cluster
