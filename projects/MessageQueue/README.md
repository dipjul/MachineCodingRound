# Message Queue
Functional requirements of this system has been described below in detail.

1. Create your own queue that will hold messages in form of JSON. Standard library queues were not allowed.
2. There was one publisher that can generate messages.
3. There are mutiple suscribers that will listen messages satisfying a particular regex.
4. Suscribers should not be tighly coupled to system and can be added or removed at runtime.
5. When a suscriber is added to the system, it registers callback function along with it. And this callback function will be invoked in case some message arrives.
6. There can be dependency relationship among suscribers i.e if there are two suscribers say A and B and A knows that B has to listen and process first, then only A can listen and process. There was many to many dependency relationship among suscribers.
7. There must a retry mechanism for handling error cases when some exception occurs in listening/ processing messages, that must be retried.
8. All Object oriented design principles must be followed and requirements should be fulfilled. Design should be flexible and scalable.