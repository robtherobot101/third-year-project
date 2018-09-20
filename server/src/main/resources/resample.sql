INSERT INTO `ACCOUNT` (`username`, `password`) VALUES ('andy', '$s0$41010$wDvDxdR4B+eq9QJvoqxOTA==$JcwQMuoWEcAmGzJA9oc/2uC8c78R9j926JyivcUlRJM=');
INSERT INTO `ACCOUNT` (`username`, `password`) VALUES ('buzz', '$s0$41010$LKb625npYcy+iWrLTf+GRA==$6hcZ2qowmblIilfwPd98FLeIM39QlYaMwAl7Wta8SnE=');
INSERT INTO `ACCOUNT` (`username`, `password`) VALUES ('mozza', '$s0$41010$VtmxoECyxmqiniFnv2iYDw==$cVNHjJs0xMUQ5wLYc3kWotM+uBxyiY5wiLo294X2WCg=');
INSERT INTO `ACCOUNT` (`username`, `password`) VALUES ('jonozilla', '$s0$41010$xaeH6eZp4wVkx2Id2hFDLQ==$n8GrCX/Jdh1oFD6vzNHpZSl4h7exoF1wxSBhVwifi2k=');
INSERT INTO `ACCOUNT` (`username`, `password`) VALUES ('mackas', '$s0$41010$/RgjJRdOhaR/NaK0VtlGkw==$aI41DS0upnUFOJi8IH4rpddItFyLsje13n9tPEX4an4=');
INSERT INTO `ACCOUNT` (`username`, `password`) VALUES ('nicky', '$s0$41010$9sEmY/HM87jXKhXS7sRmug==$E0RWTNNkmSsbfE02dqn2cYY4sSFwzrawNz82FlbXitk=');
INSERT INTO `ACCOUNT` (`username`, `password`) VALUES ('kyran', '$s0$41010$WI8J6eulTAhczc/tPc/ANQ==$FsadOJLJyoDXC9e2y4LdrtnvY7ODevWHboX7iFUzvaw=');
INSERT INTO `ACCOUNT` (`username`, `password`) VALUES ('andrew', '$s0$41010$VbCtuGCB8d3CAcDnGjUOAA==$+vWyf0N89930oowjHbrjENV8jirdoo17trRDeEnxnIc=');

INSERT INTO `USER` (`first_name`, `middle_names`, `last_name`, `preferred_name`, `preferred_middle_names`, `preferred_last_name`, `creation_time`, `last_modified`, `gender`, `gender_identity`, `height`, `weight`, `blood_type`, `current_address`, `region`, `email`, `nhi`, `blood_pressure`, `smoker_status`, `alcohol_consumption`, `date_of_birth`, `date_of_death`, `id`) VALUES
('Andy', 'Robert', 'French', 'Andy', 'Robert', 'French', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'andy@andy.com', 'ccc1115', NULL, NULL, NULL, '2018-07-19', NULL, (SELECT id FROM ACCOUNT WHERE username = 'andy')),
('Buzz', 'Buzzy', 'Knight', 'Buzz', 'Buzzy', 'Knight', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'buzz@buzz.com', 'fak2986', NULL, NULL, NULL, '2018-07-19', NULL, (SELECT id FROM ACCOUNT WHERE username = 'buzz')),
('James', 'Mozza', 'Morritt', 'James', 'Mozza', 'Morritt', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'mozza@mozza.com', 'ils1407', NULL, NULL, NULL, '2018-07-19', NULL, (SELECT id FROM ACCOUNT WHERE username = 'mozza')),
('Jono', 'Zilla', 'Hills', 'Jono', 'Zilla', 'Hills', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zilla@zilla.com', 'lsh9538', NULL, NULL, NULL, '2018-07-19', NULL, (SELECT id FROM ACCOUNT WHERE username = 'jonozilla')),
('James', 'Mackas', 'Mackay', 'James', 'Mackas', 'Mackay', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'mackas@mackas.com', 'ona0619', NULL, NULL, NULL, '2018-07-19', NULL, (SELECT id FROM ACCOUNT WHERE username = 'mackas')),
('Nicky', 'The Dark Horse', 'Zohrab-Henricks', 'Nicky', 'The Dark Horse', 'Zohrab-Henricks', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'nicky@nicky.com', 'rdj1490', NULL, NULL, NULL, '2018-07-19', NULL, (SELECT id FROM ACCOUNT WHERE username = 'nicky')),
('Kyran', 'Playing Fortnite', 'Stagg', 'Kyran', 'Playing Fortnite', 'Stagg', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'kyran@kyran.com', 'uqo1661', NULL, NULL, NULL, '2018-07-19', NULL, (SELECT id FROM ACCOUNT WHERE username = 'kyran')),
('Andrew', 'Daveo', 'Davidson', 'Andrew', 'Daveo', 'Davidson', '2018-07-19 04:38:50', '2018-07-19 04:38:50', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'andrew@andrew.com', 'xke6962', NULL, NULL, NULL, '2018-07-19', NULL, (SELECT id FROM ACCOUNT WHERE username = 'andrew'));


INSERT INTO `HOSPITAL` (`name`, `address`, `region`, `city`, `country`, `latitude`, `longitude`) VALUES
('Auckland City Hospital', '2 Park Rd', 'Auckland', 'Auckland', 'New Zealand', -36.859901, 174.769897),
('Tauranga Hospital', '829 Cameron Rd', 'Bay of Plenty', 'Tauranga', 'New Zealand', -37.707912, 176.147919),
('Whangarei Hospital', 'Maunu Rd', 'Northland', 'Whangarei', 'New Zealand', -35.735023, 174.303146),
('Waikato District Health Board', 'Pembroke Street', 'Waikato', 'Hamilton', 'New Zealand', -37.804897, 175.282059);