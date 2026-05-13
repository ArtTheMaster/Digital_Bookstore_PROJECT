package main.service;

import main.dao.CustomerDAO;
import main.model.Customer;
import main.model.Member;
import main.model.Membership;
import main.util.Constants;

public class MembershipService {
    private final CustomerDAO customerDAO;

    public MembershipService() {
        this.customerDAO = new CustomerDAO();
    }

    public double calculateDiscount(Customer customer, double totalAmount) {
        if (customer != null) {
            return customer.applyDiscount(totalAmount); // Polymorphic dispatch
        }
        return 0.0;
    }

    public void processPointsAndUpgrade(Customer customer, double purchaseAmount) {
        if (customer instanceof Member) {
            Member member = (Member) customer;
            Membership ms = member.getMembership();
            
            // Calculate points: 1 point per 100 spent
            int pointsEarned = (int) (purchaseAmount / 100);
            ms.setPoints(ms.getPoints() + pointsEarned);
            
            customerDAO.updateMembershipPoints(member.getId(), ms.getPoints());
            upgradeIfEligible(member);
        }
    }

    private void upgradeIfEligible(Member member) {
        Membership ms = member.getMembership();
        int pts = ms.getPoints();

        if (pts >= Constants.PLATINUM_POINTS && ms.getType() != Membership.MembershipType.PLATINUM) {
            customerDAO.upgradeTier(member.getId(), "PLATINUM", Constants.PLATINUM_DISCOUNT);
        } else if (pts >= Constants.GOLD_POINTS && ms.getType() == Membership.MembershipType.SILVER) {
            customerDAO.upgradeTier(member.getId(), "GOLD", Constants.GOLD_DISCOUNT);
        }
    }
}