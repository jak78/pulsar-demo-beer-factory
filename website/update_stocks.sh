#!/usr/bin/env bash
# This script does the following:
# - Ask the Warehouse service to produce beer stock level messages
# - Pause a while to let time for the messages to be produced and consumed
# - Check the stock levels on the consumer
# - Loop
# Prerequisites: curl & jq
export JQ_COLORS="1;30:0;39:0;39:0;39:0;32:1;39:1;39"
while :
do
  curl -s -X POST "http://127.0.0.1:9099/produce"
  sleep 0.2
  curl -s -X GET "http://127.0.0.1:9090/beer_stocks" |jq
  sleep 0.8
done