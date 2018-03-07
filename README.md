# Loadtest 

Kubernetes + [Locust.io](https://locust.io) based load testing harness for Ambassador.

# Setup

1. You need access to a Kubernetes cluster. I strongly recommend setting up a dedicated cluster for this tool as it is effectively designed to perform a distributed denial of service attack ("DDOS").

# Cluster Setup

You want a full-fledged Kubernetes cluster for this test and it should be configured in a very specific way. The basic requirements are:

- Two worker node groups "locust" and "main"
- The Locust slaves and master are only scheduled and run on the `locust` nodes.
- The Ambassador and Target backends are scheduled and run on the `main` nodes.

In order for deployment of the framework to work you need to ensure there are appropriate Node Labels on the nodes.

`locust` nodes must have the label `datawire.io/workload=locust`
`main` nodes must have the label `datawire.io/workload=any`

This is trivial to setup and do with a klusters template:

```yaml
---
# loadtest cluster example

kubernetesVersion: 1.8.5

# The master count should not matter for load testing.
masterCount: 1

# Two worker node groups are created in this cluster:
#
# 1.) locusts - where the locust pods run and generate load.
# 2.) main    - where envoy, ambassador and the backends run and process load.
#

workers:
  locust:
    count: 3
    machineType: m4.large
    maxPrice: 0.10
    rootVolumeSize: 10
    nodeLabels:
      "datawire.io/workload": "locust"
  main:
    count: 3
    machineType: m4.large
    maxPrice: 0.10
    rootVolumeSize: 10
    nodeLabels:
      "datawire.io/workload": "any"
```

# Installing the Load Generator

```bash
forge deploy
```

# Running the Load Generator

```bash
./locust-ui.sh
```

Connect to the forwarded port and then set the test parameters and run!

# License

Licensed under Apache 2.0. Please read [LICENSE](LICENSE) for details.
