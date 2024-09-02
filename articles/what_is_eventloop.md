# What is the Event Loop?

[Video: What the heck is the event loop anyway? - Philip Roberts](https://youtu.be/8aGhZQkoFbQ?si=Pqhk2O4Yu9zrICjQ)

You might wonder why I'm looking at this repository for Netty, but studying about the JS Event Loop.

The answer is simple: Netty uses an Event Loop! And I think understanding the JS Event Loop will help in studying Netty.

So, I've decided to study the JS Event Loop.

JS is a single-threaded language. This means it has a single-threaded call stack, which in turn means it can only do one thing at a time.

However, web APIs, such as setTimeout, AJAX calls, or DOM events, allow JS to handle asynchronous operations. When these operations are complete, their callback functions are pushed into a task queue.

This is where the Event Loop comes in. Its job is to look at the call stack and the task queue. If the call stack is empty, it takes the first task from the queue and pushes it onto the call stack, which effectively runs it.

The Event Loop allows JavaScript to perform non-blocking I/O operations, despite being single-threaded. This is crucial for maintaining a responsive user interface while handling potentially time-consuming tasks.

Now, how does this relate to Netty? Netty, a Java NIO client-server framework, also uses an Event Loop model. While the implementation details differ, the core concept is similar: efficiently handling multiple connections and I/O operations without blocking the main thread.

In Netty, each channel (representing a network connection) is assigned to an EventLoop. The EventLoop is responsible for handling all I/O events for its assigned channels throughout their lifetime. This model allows Netty to handle thousands of concurrent connections efficiently.

By understanding the JS Event Loop, we can grasp the fundamental concepts of asynchronous programming and non-blocking I/O. These principles are crucial in modern software development, especially in high-performance network applications like those built with Netty.

In conclusion, while JS and Netty operate in different environments, their use of the Event Loop concept demonstrates a shared approach to handling asynchronous operations efficiently. This understanding can provide valuable insights when working with either technology.