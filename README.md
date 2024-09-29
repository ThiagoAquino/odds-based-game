# odds-based-game

## Project Description
This project is an implementation of a simple betting game based on odds. It is developed using **Kotlin**, **Spring WebFlux**, and uses **H2 Database** for data storage. The game allows players to register, place bets on numbers between 1 and 10, and win or lose based on the game's rules as described below.

### Main Functionalities
1. **Player Registration**: A player can register with a name, surname, and a unique username.
2. **Bets**: Players can bet an amount (limited to 50% of their balance) on numbers from 1 to 10. Based on a randomly generated number, the player can:
   - Win **10x** the bet amount (if the bet number matches the generated number).
   - Win **5x** the bet amount (if the bet number is 1 higher or 1 lower than the generated number).
   - Win **50%** of the bet amount (if the bet number is 2 higher or 2 lower than the generated number).
   - Lose the bet (if the bet number is 3 or more higher/lower than the generated number).
3. **Wallet**: Players start with an initial balance of 1000 credits, and each bet affects the player's wallet accordingly.
4. **Leaderboard**: A ranking system based on players' accumulated winnings.

### System Requirements
- **Java 17**
- **Gradle**
- **H2 Database**


### API Endpoints

#### Player Endpoints
- **POST** `/player`  
  Registers a new player.
   - **Request Body**:
     ```json
     {
       "name": "Maria",
       "surname": "Silva",
       "username": "mas"
     }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "name": "Maria",
       "surname": "Silva",
       "username": "mas",
       "balance": 1000.0
     }
     ```

- **GET** `/player/{username}`  
  Retrieves the details of a player by their username.
   - **Response**:
     ```json
     {
       "id": 1,
       "name": "Maria",
       "surname": "Silva",
       "username": "mas",
       "balance": 950.0
     }
     ```

- **GET** `/player/leaderboard`  
  Returns the leaderboard of players based on their balance.
   - **Response**:
     ```json
     [
       {
         "username": "mas",
         "balance": 1500.0
       },
       {
         "username": "tas",
         "balance": 1200.0
       }
     ]
     ```

#### Bet Endpoints
- **POST** `/bet/placeBet`  
  Places a bet for a player.
   - **Request Body**:
     ```json
     {
       "username": "mas",
       "betAmount": 100,
       "betNumber": 5
     }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "playerId": 1,
       "betAmount": 100,
       "betNumber": 5,
       "generatedNumber": 7,
       "result": "LOSS",
       "winnings": 0.0
     }
     ```

- **GET** `/bet/{username}`  
  Retrieves all bets placed by a player.
   - **Response**:
     ```json
     [
       {
         "id": 1,
         "playerId": 1,
         "betAmount": 100,
         "betNumber": 5,
         "generatedNumber": 7,
         "result": "LOSS",
         "winnings": 0.0
       }
     ]
     ```

#### Transaction Endpoints
- **POST** `/transaction/deposit`  
  Deposits credits into a player's account.
   - **Request Body**:
     ```json
     {
       "playerUsername": "mas",
       "amount": 500.0
     }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "playerUsername": "mas",
       "amount": 500.0,
       "type": "DEPOSIT"
     }
     ```

- **GET** `/transaction/{username}`  
  Retrieves all transactions made by a player.
   - **Response**:
     ```json
     [
       {
         "id": 1,
         "playerUsername": "mas",
         "amount": 500.0,
         "type": "DEPOSIT"
       }
     ]
     ```

### How to Run the Application Locally

1. **Clone the repository**:
   ```bash
   git clone https://github.com/ThiagoAquino/Odds-based-game
   cd Odds-based-game

2. **Compile the project**:
    ```bash
   ./gradlew build

3. **Run the application**:
    ```bash
   ./gradlew bootRun

4. **Running Unit Tests**:
    ```
   ./gradlew test

### How to Run the Application with Docker(Optional)

1. **Build the Docker image**:
   ```bash
   docker build -t odds-based-game .

2. **Run the container docker**:
    ```bash
   docker run -p 8080:8080 odds-based-game


Now, you can access the application at http://localhost:8080.

This README provides detailed instructions and descriptions for a Kotlin project using Gradle, Spring WebFlux, and H2 Database.