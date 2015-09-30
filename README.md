# Resource Agents Project


# Context #
This is the project for the course "Introduction to Multi Agent Systems" at TU Kaiserslautern, Germany.

|                          |                                                                                                                              |
| -------------------- | ------------------------------------------------------------------------------------------------------- |
| **Course:**       | Introduction to Multi Agent Systems                                |
| **Semester:**   |  SS 2015                                                                                                              |
| **Professors:** |  [Oliver Wendt](https://bisor.wiwi.uni-kl.de/team/prof-wendt/), Michael Schwind |
| **Students:**    |  Stefan Hemmer, Ghazanfar Abbas, Naveena Baturan     |

# Description #
A “customer order” is offered to you of which the price it fixed, i.e. you are confronted with a stochastic dynamic decision process, here with a known number of “take it or leave it” requests. Your goal is to maximise the total revenue collected with respect to your limited resources (think of a hotel with six rooms for different days for example)

Assume now, that you cannot fulfil the service requests by yourself, but you rather have to subcontract each of the order’s resource requirements to individual economic agents, each of them demanding their own price for the occupation of her/his resource.

E.g. assume you are a travel agent asked by the customer to book a multi-city-trip and you have to book different hotels for one night each in different cities (different room quantities)

If the sum of what the hotels charge is more than what the customer is willing to pay, you would probably rather reject the offer instead of incurring a loss.

So let us set up pricing strategies for the individual resource agents (hotels):

If they charge too much, the order might get lost.
If they charge too little, the profit will be too low.

However: If only one agent (maybe owning a bottleneck resource?) charges a high price while the others offer low prices, the first may be lucky as the sum is still below the customer’s willingness to pay !?

We need teams (à 2 persons) to specify and implement either
a) the decision logic of the individual resource owner.
b) the mechanism of the “service aggregator” (the travel agent in the example)

You should be creative but having incentive compatibility in mind might be a good advice! 

It might also be wise to first assume that there is only single “monopolistic" agent for each resource (one hotel in every city) and then checking what happens if there is competition, i.e. multiple offerings for a given type of resource. 
What happens if there is an owner who ownes hotels in two different cities. How does this phenomenon influence the outcome of the auction and the strategies of the owners? 
