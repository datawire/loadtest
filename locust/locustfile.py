#!/usr/bin/env python

from locust import HttpLocust, TaskSet, task


class ApiTasks(TaskSet):

    def simple_http_endpoint(self):
        self.client.get("/")


class ApiUser(HttpLocust):
    task_set = ApiTasks
