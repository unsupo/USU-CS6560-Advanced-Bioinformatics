while read l; do if [ "a" == "a`ssh-keygen -H -F $l`" ]; then echo $l; fi; done < cluster_hosts
