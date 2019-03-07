package com.plivo.contact.app;

import com.plivo.contact.auth.Authorization;
import com.plivo.contact.auth.AuthorizationFactory;
import com.plivo.contact.auth.Session;
import com.plivo.contact.handler.AccountHandler;
import com.plivo.contact.handler.ContactHandler;
import com.plivo.contact.handler.database.DatabaseHandler;
import com.plivo.contact.model.StringResponse;
import com.plivo.contact.model.UserAccount;
import com.plivo.contact.model.UserContact;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class APIController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/text")
    @ResponseBody
    public ResponseEntity getContacts() {
        String api = "/account\n" +
                "   POST : Create an account\n" +
                "       Request Payload\n" +
                "           user : Account user name\n" +
                "           password : Login password\n" +
                "\n" +
                "/contact\n" +
                "   GET : Get list of contacts\n" +
                "       Request Header\n" +
                "           Authorization : Basic authorization header\n" +
                "       Request Parameter\n" +
                "           name : Search with name\n" +
                "           email : Search with email\n" +
                "           page-limit : Page size\n" +
                "           page-link : Previous page token\n" +
                "\n" +
                "   POST : Create a contact\n" +
                "       Request Header\n" +
                "           Authorization : Basic authorization header\n" +
                "       Request Payload\n" +
                "           email : Contact email\n" +
                "           name : Contact name\n" +
                "           phone : Contact phone number(string)\n" +
                "           relation : Relationship with contact\n" +
                "\n" +
                "/contact/{contact-email}\n" +
                "   GET : Get single contact\n" +
                "       Request Header\n" +
                "           Authorization : Basic authorization header\n" +
                "\n" +
                "   PUT : Update contact(all properties)\n" +
                "       Request Header\n" +
                "           Authorization : Basic authorization header\n" +
                "       Request Payload\n" +
                "           Contact details\n" +
                "\n" +
                "   PATCH : Update contact(partial)\n" +
                "       Request Header\n" +
                "           Authorization : Basic authorization header\n" +
                "       Request Payload\n" +
                "           Contact details\n" +
                "\n" +
                "   DELETE : Delete a contact\n" +
                "       Request Header\n" +
                "           Authorization : Basic authorization header";
        return ResponseEntity.ok(api);
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity getContacts(@RequestHeader(value = "Authorization") String auth,
                                      @RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "email", required = false) String email,
                                      @RequestParam(value = "page-limit", required = false) Integer pageSize,
                                      @RequestParam(value = "page-link", required = false) String pageLink) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        Session session = null;
        try {
            Authorization authorization = AuthorizationFactory.getAuthorizationImpl(auth);
            session = authorization.authenticate(databaseHandler);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(authorization.getFailureReason()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(ContactHandler.getContacts(databaseHandler, session.getAccount(), email, name
                , pageSize, pageLink));
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity createContact(@RequestHeader(value = "Authorization") String auth,
                                        @RequestBody UserContact contact) {

        DatabaseHandler databaseHandler = new DatabaseHandler();
        Session session = null;
        try {
            Authorization authorization = AuthorizationFactory.getAuthorizationImpl(auth);
            session = authorization.authenticate(databaseHandler);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(authorization.getFailureReason()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(e.getMessage()));
        }

        try {
            ContactHandler.createContact(databaseHandler, session.getAccount(), contact);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StringResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new StringResponse("Contact created successfully"));
    }

    @RequestMapping(value = "/contact/{contactEmail}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity getContact(@RequestHeader(value = "Authorization") String auth,
                                     @PathVariable(value = "contactEmail") String contactEmail) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        Session session = null;
        try {
            Authorization authorization = AuthorizationFactory.getAuthorizationImpl(auth);
            session = authorization.authenticate(databaseHandler);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(authorization.getFailureReason()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(e.getMessage()));
        }

        UserContact contact = ContactHandler.getContact(databaseHandler, session.getAccount(), contactEmail);
        if (contact == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponse("Contact does not exists"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(contact);
    }

    @RequestMapping(value = "/contact/{contactEmail}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public ResponseEntity updateContact(@RequestHeader(value = "Authorization") String auth,
                                        @PathVariable(value = "contactEmail") String contactEmail,
                                        @RequestBody UserContact contact) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        Session session = null;
        try {
            Authorization authorization = AuthorizationFactory.getAuthorizationImpl(auth);
            session = authorization.authenticate(databaseHandler);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(authorization.getFailureReason()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(e.getMessage()));
        }

        try {
            ContactHandler.updateContact(databaseHandler, session.getAccount(), contactEmail, contact, false);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @RequestMapping(value = "/contact/{contactEmail}", method = RequestMethod.PATCH, produces = "application/json")
    @ResponseBody
    public ResponseEntity updateContactPartial(@RequestHeader(value = "Authorization") String auth,
                                               @PathVariable(value = "contactEmail") String contactEmail,
                                               @RequestBody UserContact contact) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        Session session = null;
        try {
            Authorization authorization = AuthorizationFactory.getAuthorizationImpl(auth);
            session = authorization.authenticate(databaseHandler);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(authorization.getFailureReason()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(e.getMessage()));
        }

        try {
            ContactHandler.updateContact(databaseHandler, session.getAccount(), contactEmail, contact, true);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new StringResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @RequestMapping(value = "/contact/{contactEmail}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResponseEntity deleteContact(@RequestHeader(value = "Authorization") String auth,
                                        @PathVariable(value = "contactEmail") String contactEmail) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        Session session = null;
        try {
            Authorization authorization = AuthorizationFactory.getAuthorizationImpl(auth);
            session = authorization.authenticate(databaseHandler);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(authorization.getFailureReason()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new StringResponse(e.getMessage()));
        }

        ContactHandler.deleteContact(databaseHandler, session.getAccount(), contactEmail);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @RequestMapping(value = "/account", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity createAccount(@RequestBody UserAccount loginUser) {
        DatabaseHandler databaseHandler = new DatabaseHandler();
        try {
            AccountHandler.createAccount(databaseHandler, loginUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new StringResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new StringResponse("Account created successfully"));
    }
}
