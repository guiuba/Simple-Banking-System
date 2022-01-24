# Simple-Banking-System
Offering basic banking operations, uses Luhn algorithm to verify card number validity and performs JDBC interaction with a SQlite database.
------------------------------------------------
This is my implementation of Jet Brains Simple-Banking-System project stage 4/4.
How program works:

(Imagem: JetBrains Academy / Hyperskill logo)
 Study plan
 Map
 Track 
Examples
The symbol > represents the user input. Notice that it's not a part of the input.
Example 1:
4000009455296122
1961
4000003305160034
5639
>4000009455296122
>1961
Enter income:
>10000
Income was added!
Balance: 10000
>3
Transfer
Enter card number:
>4000003305160035
Probably you made a mistake in the card number. Please try again!
>3
Transfer
Enter card number:
>4000003305061034
Such a card does not exist.
>3
Transfer
Enter card number:
>4000003305160034
Enter how much money you want to transfer:
>15000
Not enough money!
>3
Transfer
Enter card number:
>4000003305160034
Enter how much money you want to transfer:
>5000
Success!
Balance: 5000
Bye!
Example 2:
1. Create an account
2. Log into account
0. Exit
>1
Your card has been created
Your card number:
4000007916053702
Your card PIN:
6263
1. Create an account
2. Log into account
0. Exit
>2
Enter your card number:
>4000007916053702
Enter your PIN:
>6263
You have successfully logged in!
1. Balance
2. Add income
3. Do transfer
4. Close account
5. Log out
0. Exit
>4
The account has been closed!
1. Create an account
2. Log into account
0. Exit
>2
Enter your card number:
>4000007916053702
Enter your PIN:
>6263
Wrong card number or PIN!
1. Create an account
2. Log into account
0. Exit
>0
Bye!
![image](https://user-images.githubusercontent.com/69851038/150774675-d61394a3-c118-4247-bd41-d13d12b8efeb.png)

