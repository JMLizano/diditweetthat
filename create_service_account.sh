#! /bin/bash

PROJECT_ID=diditweetthat-272410
NAME=diditweetthat

gcloud config set project $PROJECT_ID
gcloud iam service-accounts create $NAME
gcloud projects add-iam-policy-binding \
     $PROJECT_ID \
     --member "serviceAccount:$NAME@$PROJECT_ID.iam.gserviceaccount.com" \
     --role "roles/owner"
gcloud iam service-accounts keys create \
    ${NAME}.json \
    --iam-account $NAME@$PROJECT_ID.iam.gserviceaccount.com