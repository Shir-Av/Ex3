# The Maze Of Waze


This project represents a game where a group of robots needs to perform movement tasks on a weighted graph in order to collect fruits (of varying value) so that all the fruits collected will be of maximum value.
This assignment data structures and algorithms are based on previous project Ex2: https://github.com/Shir-Av/Ex2 .

**How To Play**

1. Run the game and select game mode: manual or automatic

<img width="301" alt="openWindow" src="https://user-images.githubusercontent.com/58072903/72681129-404f5580-3ac9-11ea-8197-dca8d590c959.png">

2. In each game mode, select the desired level to play

<img width="946" alt="manual 3" src="https://user-images.githubusercontent.com/58072903/72682175-08e5a680-3ad3-11ea-871c-1ae7b1854cf0.png">

3. **In automatic mode :**

    In this mode the game will work automatically according to algorithms we have developed to gain the most points for this stage.
    The robots will be strategically placed, according to an algorithm, at the Node closest to the fruit and will move to the next Node     according to a calculation of the fruit closest to them.
    
<img width="949" alt="1" src="https://user-images.githubusercontent.com/58072903/72685486-478b5900-3af3-11ea-940d-ccdb539616d4.png">
    
4. **In manual mode :**

In this mode the game will work manually according to the player's choice.
After selecting the stage Please follow these steps:

- A window will open, listing the number of robots for that stage, you will be required to choose the initial position of each robot on the graph.

<img width="949" alt="manual 1" src="https://user-images.githubusercontent.com/58072903/72681463-9671c800-3acc-11ea-8da8-6ae30c8cab70.png">


-  After the initial placement of the robots, click on each robot and select its next target according to the possible "neighbors" list.

<img width="949" alt="3" src="https://user-images.githubusercontent.com/58072903/72685578-25460b00-3af4-11ea-9300-f58971205149.png">

- At the end of the game a window will open and show the score.

<img width="948" alt="2" src="https://user-images.githubusercontent.com/58072903/72685519-b1a3fe00-3af3-11ea-8175-8aa9c7d37af8.png">


- **Note that each stage is timed and the goal of the game is to get the highest score by eating as much fruit as possible.**


**The project includes the following class:**

- Fruit class:

This class represents the set of operations applicable on a fruit.

- Robot class:

This class represents the set of operations applicable on a robot.

- GameClient:

This class represents the auto-play scenario.
This class uses a "server" (jar file) named GameServer, which we perform in-game operations.

- MyGameGUI:

This class represents a Graphical User Interface - GUI of the game.
In this class the manual game scenario is realized.
This class uses a "server" (jar file) named GameServer, which we perform in-game operations.

- KML_Logger:

This class allows you to export the graph, bots and fruits to a KML file



**for more info of the project go to wiki.**
