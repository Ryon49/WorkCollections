This is a course project of CS 122B, Projects in Databases and Web Applications



Fablix is a movie browsing website that allows users to search movie with movie title, director, year, and star. It also has shopping cart functionality that allows user to generate sale transaction (despite viewing sale transaction is not implemented yet). It also allows admin user to add new star and movie to existing backend database. 



The project architecture uses React to build frontend interface and Spring to build serve-side application. All traffics are communicated through Http Request (rest api). 



The frontend app primarily uses a design deviated from entity–component–system (ECS) in game dev. Because all my pages are splited by CSS grid layout, the design should be explained as Component-Layout-View. The app uses reactstrap (Bootstrap 5 in react) for interface design and react-router (I love it, its so simple) for page navigation. However, there are quite amount of scenarios where I pass function as properties to component. The design decision behind this is that I try to aggregate all mutable function calls into a class so that the purpose of component is just to represent data. But this introduces a few dependency issues where some functions may be passed down into child of child of components and this reduces code readability. I suspect that react-redux is a good way to solve this problem but it requires me to basically rewrite entire projects.....



The backend app uses classic MVC architecture. 




