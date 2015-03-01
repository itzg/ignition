# ignition

Gets your iPXE and cloud-config'ed clusters up and running in no time.

## Getting Started

Build it:

    mvn package

Run it:

    java -jar target/ignition-*.jar \
      --etcd.uris=http://ETCD_HOST:4001 \
      --images.baseDir=YOUR_IMAGES_DIR \
      --ignition.publishAs=http://YOUR_HOST:8080

where

* `ETCD_HOST` would be the one of the hosts in your existing etcd cluster. `--etcd.uris` can also
  accept a comma-separated list of URLs to allow auto-failover to other members of the cluster
* `YOUR_IMAGES_DIR` is a directory that contains vmlinuz, cpio, and other image/ISO type files that will be
  served up under `http://YOUR_HOST:8080/images`.
* `YOUR_HOST` is the LAN accessible hostname/IP of your Ignition server host