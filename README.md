# snakedl4j - Simple AI made using Deeplearning4j trying to play Snake game

Aim of this project is to introduce you to dl4j while we try to train AI to play snake game. Keep in mind that this is mine first contact with dl4j and that there might be things
done wrong and in incorect way. With that beeing said lets take a look on how it is implemented.

## What does the snake see ?
How far the snake can see is controled by a `VIEW_DISTANCE` property which by default is set to `3`.
![Image](https://raw.githubusercontent.com/liliumbosniacum/snakedl4j/master/src/main/resources/images/snake.PNG)

Snake is always aware if it is safe to go up, down, left or right as well as where is the food. This information is used as an input to the neural network.

## How to start the training
In order to start the training application needs to be started with program argument `TRAIN`. Number of training sessions (how long will the training last) is defined in `NetworkTrainingHelper#NUMBER_OF_GAMES`.

## How to evaluate trained network
In order to start the training application needs to be started with program argument `EVALUATE`. Number of evaluation sessions is defined in `NetworkEvaluationHelper#NUMBER_OF_GAMES`.

## Preview
https://youtu.be/vH9qlZcifZk
