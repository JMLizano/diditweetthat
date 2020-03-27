# CI/ID

Two main options:
    - Everything happens in Github: Github actions, CircleCI, etc.
        Pros:
            - Only one place for configurations
            - Only one syntax to learn
            - You can execute it locally, although not being official
            - Probably more fine grained control over when to trigger CI/CD
    - CI happens in Github and CD in Google cloud Build
        Pros:
            - Feels right to use Github for CI, since Github focus is on code
            itself and Google Cloud for CD, since Google focus is on deployment.
            - Official way for running locally
            - You don't have to share any deployment keys (GKE, gcloud shell, etc.)
            with Github.
            - Official documentation on how to do the deployment